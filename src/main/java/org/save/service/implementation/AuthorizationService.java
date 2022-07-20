package org.save.service.implementation;

import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.save.exception.EmailIsInUseException;
import org.save.model.dto.auth.SignInRequest;
import org.save.model.dto.auth.SignUpRequest;
import org.save.model.dto.auth.TokenResponse;
import org.save.model.entity.common.Portfolio;
import org.save.model.entity.common.RoleEntity;
import org.save.model.entity.common.User;
import org.save.model.entity.common.Wallet;
import org.save.model.enums.AccountStatusEnum;
import org.save.model.enums.ReferralCause;
import org.save.model.enums.RoleEnum;
import org.save.model.enums.VerificationStep;
import org.save.repo.UserRepository;
import org.save.service.finance.ReferralService;
import org.save.service.watchlist.WatchlistService;
import org.save.util.StringUtils;
import org.save.util.cryptocurrency.BitcoinsService;
import org.save.util.cryptocurrency.CryptoAssetTicker;
import org.save.util.cryptocurrency.EthereumService;
import org.save.util.cryptocurrency.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService {

  private final UserService userService;
  private final WalletService walletService;
  private final PortfolioService portfolioService;
  private final WatchlistService watchlistService;
  private final ReferralService referralService;
  private final UserRepository userRepository;
  private final CodeSender codeSender;
  private final EthereumService ethereumService;
  private final PasswordEncoder passwordEncoder;
  private final AssetService assetService;
  private final BitcoinsService bitcoinsService;
  private final TokenService tokenService;
  private final JwtTokenService jwtTokenService;
  private final RoleService roleService;

  public TokenResponse signInUser(SignInRequest signInRequest, Integer securityCode) {
    String username = signInRequest.getUsername();
    var user =
        userRepository
            .findUserByEmail(signInRequest.getEmail())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "User not found for email: " + signInRequest.getEmail()));
    if (username == null) {
      username = user.getUsername();
    }

    if (!codeSender.codeIsValid(username, securityCode)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid security code");
    }

    TokenResponse tokenResponse =
        jwtTokenService.createTokenResponse(
            username, signInRequest.getPassword(), roleService.getUserAuthorities(user));
    codeSender.remove(username);
    return tokenResponse;
  }

  // TODO: complexity is too high
  @SneakyThrows
  @Transactional
  public TokenResponse signUpUser(
      SignUpRequest signUpRequest, Integer activationCode, String parentReferralLink) {
    if (!codeSender.codeIsValid(signUpRequest.getEmail(), activationCode)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid activation code");
    }

    var username = StringUtils.generateUsername();
    var role = roleService.getRoleByName(RoleEnum.ROLE_USER);
    Set<RoleEntity> roles = new HashSet<>();
    roles.add(role);
    var user =
        User.builder()
            .username(username)
            .email(signUpRequest.getEmail())
            .password(passwordEncoder.encode(signUpRequest.getPassword()))
            .phone(signUpRequest.getPhone())
            .roleEntitySet(roles)
            .accountStatus(AccountStatusEnum.REGULAR)
            .build();

    userService.saveUser(user);

    createReferralLink(user);
    createWallet(user);
    createPortfolio(user);
    createCryptoAssetByPortfolio(user.getPortfolio());

    user.setVerified(VerificationStep.NOT_VERIFIED);

    watchlistService.createWatchlistForUser(user);

    user = userService.saveUser(user);

    codeSender.remove(signUpRequest.getEmail());

    ethereumService.getAddress(user.getId());

    // In order to avoid exception on Tatum (429 Too Many Requests)
    Thread.sleep(1000);
    bitcoinsService.getAddress(user.getId());

    Thread.sleep(1000);
    tokenService.getAddress(user.getId());

    if (parentReferralLink != null
        && referralService.checkReferralLinkIsValid(user.getReferralLink())) {
      Long userId = userRepository.findUserIdByReferralLink(parentReferralLink);
      user.setParentId(userId);
      referralService.sendTokensByReferralLink(parentReferralLink);
    }

    referralService.sendTokensFromOurWallet(
        user.getId(), ReferralCause.FOR_REGISTRY.getAmount(), ReferralCause.FOR_REGISTRY);

    return jwtTokenService.createTokenResponse(
        username, signUpRequest.getPassword(), roleService.getUserAuthorities(user));
  }

  public void sendActivationCode(SignUpRequest signUpRequest) {
    if (userService.existsUserByEmail(signUpRequest.getEmail())) {
      throw new EmailIsInUseException("Email is already in use: " + signUpRequest.getEmail());
    }
    codeSender.send(signUpRequest.getEmail(), signUpRequest.getEmail(), "Activation code");
  }

  public void sendSecurityCode(SignInRequest signInRequest) {
    User user;

    if (signInRequest.getUsername() == null) {
      user =
          userService
              .getUserByEmail(signInRequest.getEmail())
              .orElseThrow(
                  () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email"));
    } else {
      user = userService.getUserByUsername(signInRequest.getUsername());
    }

    if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password");
    }

    codeSender.send(user.getUsername(), user.getEmail(), "Security code");
  }

  // TODO: move all these creations to another services
  private void createReferralLink(User user) {
    String referralLink = referralService.createReferralLink();
    user.setReferralLink(referralLink);
  }

  private void createWallet(User user) {
    Wallet wallet = walletService.createWallet(user);
    user.setWallet(wallet);
  }

  private void createPortfolio(User user) {
    Portfolio portfolio = portfolioService.createPortfolio(user);
    user.setPortfolio(portfolio);
  }

  private void createCryptoAssetByPortfolio(Portfolio portfolio) {
    assetService.createCryptoAsset(portfolio, CryptoAssetTicker.BTC);
    assetService.createCryptoAsset(portfolio, CryptoAssetTicker.ETH);
    assetService.createCryptoAsset(portfolio, CryptoAssetTicker.CLSH);
  }
}
