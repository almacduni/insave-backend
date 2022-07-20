package org.save.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.save.RandomUtil.randomEmail;
import static org.save.RandomUtil.randomInt;
import static org.save.RandomUtil.randomPhone;
import static org.save.RandomUtil.randomString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.save.model.dto.auth.SignInRequest;
import org.save.model.dto.auth.SignUpRequest;
import org.save.model.entity.common.User;
import org.save.repo.UserRepository;
import org.save.service.finance.ReferralService;
import org.save.service.implementation.AssetService;
import org.save.service.implementation.AuthorizationService;
import org.save.service.implementation.CodeSender;
import org.save.service.implementation.JwtTokenService;
import org.save.service.implementation.PortfolioService;
import org.save.service.implementation.UserService;
import org.save.service.implementation.WalletService;
import org.save.service.watchlist.WatchlistService;
import org.save.util.cryptocurrency.BitcoinsService;
import org.save.util.cryptocurrency.EthereumService;
import org.save.util.cryptocurrency.TokenService;
import org.save.util.jwt.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

public class AuthorizationServiceTest {

  @Mock private UserService userService;
  @Mock private UserRepository userRepository;
  @Mock private CodeSender codeSender;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private ReferralService referralService;
  @Mock private WalletService walletService;
  @Mock private PortfolioService portfolioService;
  @Mock private AssetService assetService;
  @Mock private WatchlistService watchlistService;
  @Mock private EthereumService ethereumService;
  @Mock private BitcoinsService bitcoinsService;
  @Mock private TokenService tokenService;
  @Mock private AuthenticationManager authenticationManager;
  @Mock private JwtUtils jwtUtils;
  @Mock private JwtTokenService jwtTokenService;
  @InjectMocks private AuthorizationService authorizationService;
  private User user;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);

    String userName = randomString();
    String email = randomEmail();
    String password = randomString();
    String phone = randomPhone();
    user = new User(userName, email, password, phone);
    user.setId((long) randomInt());
  }

  //  @Test
  //  public void signInUserSuccessTest() {
  //    int securityCode = randomInt();
  //    String token = randomString();
  //    String refreshToken = randomString();
  //
  //    when(codeSender.codeIsValid(user.getUsername(), securityCode)).thenReturn(true);
  //    when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(null);
  //    when(userRepository.findUserByEmail(anyString()))
  //        .thenReturn(java.util.Optional.ofNullable(user));
  //    var tokenResp = new TokenResponse(token, refreshToken);
  //    when(jwtTokenService.createTokenResponse(anyString(), anyString(), any()))
  //        .thenReturn(tokenResp);
  //    doNothing().when(codeSender).remove(user.getUsername());
  //
  //    TokenResponse tokenResponse =
  //        authorizationService.signInUser(
  //            new SignInRequest(user.getUsername(), user.getEmail(), user.getPassword()),
  //            securityCode);
  //
  //    assertEquals(token, tokenResponse.getToken());
  //    assertEquals(refreshToken, tokenResponse.getRefreshToken());
  //    verify(codeSender, times(1)).codeIsValid(user.getUsername(), securityCode);
  //    verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
  //    verify(jwtTokenService, times(1)).createTokenResponse(anyString(), anyString(), any());
  //    verify(codeSender, times(1)).remove(user.getUsername());
  //  }

  @Test
  public void signInUserFailureTest() {
    assertThrows(
        ResponseStatusException.class,
        () ->
            authorizationService.signInUser(
                new SignInRequest(null, user.getEmail(), user.getPassword()), randomInt()),
        "User not found");

    when(userService.existsUserByEmail(user.getEmail())).thenReturn(false);
    assertThrows(
        ResponseStatusException.class,
        () ->
            authorizationService.signInUser(
                new SignInRequest(user.getUsername(), user.getEmail(), user.getPassword()),
                randomInt()),
        "Invalid security code");
  }

  @Test
  public void generateNewTokensSuccessTest() {
    String refreshToken = randomString();
    String newToken = randomString();
    String newRefreshToken = randomString();

    when(jwtUtils.extractJwtToken("Bearer " + refreshToken)).thenReturn(refreshToken);
    doNothing().when(jwtUtils).validateJwtToken(refreshToken);
    when(jwtUtils.getUsernameFromToken(refreshToken)).thenReturn(user.getUsername());
    when(jwtUtils.getTokenType(refreshToken)).thenReturn("refresh");
    //    when(jwtUtils.generateToken(user.getUsername())).thenReturn(newToken);
    //    when(jwtUtils.generateRefreshToken(user.getUsername())).thenReturn(newRefreshToken);
    //
    //    TokenResponse tokenResponse = authorizationService.generateNewTokens("Bearer " +
    // refreshToken);

    //    assertEquals(newToken, tokenResponse.getToken());
    //    assertEquals(newRefreshToken, tokenResponse.getRefreshToken());
  }

  @Test
  public void generateNewTokensFailureTest() {
    String refreshToken = randomString();

    when(jwtUtils.extractJwtToken("Bearer " + refreshToken)).thenReturn(refreshToken);
    doNothing().when(jwtUtils).validateJwtToken(refreshToken);
    when(jwtUtils.getUsernameFromToken(refreshToken)).thenReturn(user.getUsername());
    when(jwtUtils.getTokenType(refreshToken)).thenReturn(randomString());

    //    assertThrows(
    //        InvalidTokenException.class,
    //        () -> authorizationService.generateNewTokens("Bearer " + refreshToken),
    //        "Expected JWT type is 'refresh'");
  }

  //  @Test
  //  public void signUpUserSuccessTest() {
  //    int activationCode = randomInt();
  //    String encodedPassword = randomString();
  //    String referralLink = randomString();
  //    String parentReferralLink = randomString();
  //    Wallet wallet = new Wallet();
  //    Portfolio portfolio = new Portfolio();
  //    Asset asset = new Asset();
  //    Watchlist watchlist = new Watchlist();
  //    String address = randomString();
  //    int parentId = randomInt();
  //    String token = randomString();
  //    String refreshToken = randomString();
  //
  //    user.setReferralLink(referralLink);
  //    user.setParentId((long) parentId);
  //    user.setWallet(wallet);
  //    user.setPortfolio(portfolio);
  //
  //    when(userService.existsUserByEmail(user.getEmail())).thenReturn(false);
  //    when(codeSender.codeIsValid(user.getEmail(), activationCode)).thenReturn(true);
  //    when(passwordEncoder.encode(user.getPassword())).thenReturn(encodedPassword);
  //    when(userService.saveUser(any(User.class))).thenReturn(user);
  //    when(referralService.createReferralLink()).thenReturn(referralLink);
  //    when(walletService.createWallet(user)).thenReturn(wallet);
  //    when(portfolioService.createPortfolio(user)).thenReturn(portfolio);
  //    when(assetService.createCryptoAsset(any(Portfolio.class), any(CryptoAssetTicker.class)))
  //        .thenReturn(asset);
  //    when(watchlistService.createWatchlistForUser(user)).thenReturn(watchlist);
  //    doNothing().when(codeSender).remove(user.getEmail());
  //    when(ethereumService.getAddress(user.getId())).thenReturn(address);
  //    when(bitcoinsService.getAddress(user.getId())).thenReturn(address);
  //    when(tokenService.getAddress(user.getId())).thenReturn(address);
  //    when(referralService.checkReferralLinkIsValid(referralLink)).thenReturn(true);
  //    when(userRepository.findUserIdByReferralLink(parentReferralLink)).thenReturn((long)
  // parentId);
  //    doNothing().when(referralService).sendTokensByReferralLink(parentReferralLink);
  //    doNothing()
  //        .when(referralService)
  //        .sendTokensFromOurWallet(
  //            user.getId(), BigDecimal.valueOf(0.05), ReferralCause.FOR_REGISTRY);
  //    when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(null);
  //
  //    TokenResponse tokenResponse =
  //        authorizationService.signUpUser(
  //            new SignUpRequest(user.getPassword(), user.getEmail(), user.getPhone()),
  //            activationCode,
  //            parentReferralLink);
  //
  //    assertEquals(token, tokenResponse.getToken());
  //    assertEquals(refreshToken, tokenResponse.getRefreshToken());
  //    verify(userService, times(1)).existsUserByEmail(user.getEmail());
  //    verify(codeSender, times(1)).codeIsValid(user.getEmail(), activationCode);
  //    verify(passwordEncoder, times(1)).encode(user.getPassword());
  //    verify(userService, times(2)).saveUser(any(User.class));
  //    verify(referralService, times(1)).createReferralLink();
  //    verify(referralService, times(1)).createReferralLink();
  //    verify(walletService, times(1)).createWallet(user);
  //    verify(portfolioService, times(1)).createPortfolio(user);
  //    verify(portfolioService, times(1)).createPortfolio(user);
  //    verify(assetService, times(3))
  //        .createCryptoAsset(any(Portfolio.class), any(CryptoAssetTicker.class));
  //    verify(watchlistService, times(1)).createWatchlistForUser(user);
  //    verify(codeSender, times(1)).remove(user.getEmail());
  //    verify(ethereumService, times(1)).getAddress(user.getId());
  //    verify(bitcoinsService, times(1)).getAddress(user.getId());
  //    verify(tokenService, times(1)).getAddress(user.getId());
  //    verify(referralService, times(1)).checkReferralLinkIsValid(referralLink);
  //    verify(userRepository, times(1)).findUserIdByReferralLink(parentReferralLink);
  //    verify(referralService, times(1)).sendTokensByReferralLink(parentReferralLink);
  //    verify(referralService, times(1))
  //        .sendTokensFromOurWallet(
  //            user.getId(), BigDecimal.valueOf(0.05), ReferralCause.FOR_REGISTRY);
  //    verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
  //  }

  @Test
  public void signUpUserFailureTest() {
    when(userService.existsUserByEmail(user.getEmail())).thenReturn(false);

    assertThrows(
        ResponseStatusException.class,
        () ->
            authorizationService.signUpUser(
                new SignUpRequest(user.getPassword(), user.getEmail(), user.getPhone()),
                randomInt(),
                null),
        "Invalid activation code");
  }

  @Test
  public void sendActivationCodeTest() {
    String message = "Activation code";

    when(userService.existsUserByEmail(user.getEmail())).thenReturn(false);
    doNothing().when(codeSender).send(user.getEmail(), user.getEmail(), message);

    authorizationService.sendActivationCode(
        new SignUpRequest(user.getPassword(), user.getEmail(), user.getPhone()));

    verify(userService, times(1)).existsUserByEmail(user.getEmail());
    verify(codeSender, times(1)).send(user.getEmail(), user.getEmail(), message);
  }

  @Test
  public void sendSecurityCodeSuccessTest() {
    String message = "Security code";

    when(userService.getUserByUsername(user.getUsername())).thenReturn(user);
    when(passwordEncoder.matches(any(CharSequence.class), any(String.class))).thenReturn(true);
    doNothing().when(codeSender).send(user.getUsername(), user.getEmail(), message);

    authorizationService.sendSecurityCode(
        new SignInRequest(user.getUsername(), user.getEmail(), user.getPassword()));

    verify(userService, times(1)).getUserByUsername(user.getUsername());
    verify(codeSender, times(1)).send(user.getUsername(), user.getEmail(), message);
  }

  @Test
  public void sendSecurityCodeFailureTest() {
    assertThrows(
        ResponseStatusException.class,
        () ->
            authorizationService.sendSecurityCode(
                new SignInRequest(null, randomEmail(), randomString())),
        "Invalid email");

    when(userService.getUserByUsername(user.getUsername())).thenReturn(user);
    assertThrows(
        ResponseStatusException.class,
        () ->
            authorizationService.sendSecurityCode(
                new SignInRequest(user.getUsername(), user.getEmail(), randomString())),
        "Invalid password");
  }

  //  @Test
  //  public void getCurrentUserTest() {
  //    String token = randomString();
  //
  //    when(jwtUtils.extractJwtToken("Bearer " + token)).thenReturn(token);
  //    when(jwtUtils.getUsernameFromToken(token)).thenReturn(user.getUsername());
  //
  //    assertThrows(
  //        NoSuchObjectException.class,
  //        () -> userService.getCurrentUser("Bearer " + token),
  //        "There is no user with such username");
  //  }
}
