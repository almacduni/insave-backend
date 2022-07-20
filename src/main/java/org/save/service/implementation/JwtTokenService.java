package org.save.service.implementation;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.save.exception.InvalidTokenException;
import org.save.exception.NoSuchObjectException;
import org.save.model.dto.auth.TokenResponse;
import org.save.repo.UserRepository;
import org.save.util.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

  private static final String REFRESH_TOKEN_TYPE = "refresh";
  private static final String ACCESS_TOKEN_TYPE = "access";

  private final JwtUtils jwtUtils;
  private final UserRepository userRepository;
  private final RoleService roleService;
  private final AuthenticationManager authenticationManager;

  @Value("${insave.user-management.jwt.jwtTokenExpirationMs}")
  private int jwtTokenExpirationMs;

  @Value("${insave.user-management.jwt.jwtRefreshTokenExpirationMs}")
  private int jwtRefreshTokenExpirationMs;

  public TokenResponse createTokenResponse(
      String username, String password, Collection<? extends GrantedAuthority> authorities) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password, authorities));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    return new TokenResponse(
        generateTokenByAuthentication(authentication, ACCESS_TOKEN_TYPE),
        generateTokenByAuthentication(authentication, REFRESH_TOKEN_TYPE));
  }

  public TokenResponse generateNewTokens(String authorizationHeader) {
    var refreshToken = jwtUtils.extractJwtToken(authorizationHeader);
    jwtUtils.validateJwtToken(refreshToken);
    String usernameFromToken = jwtUtils.getUsernameFromToken(refreshToken);
    String tokenType = jwtUtils.getTokenType(refreshToken);
    if (!REFRESH_TOKEN_TYPE.equals(tokenType)) {
      throw new InvalidTokenException("Expected JWT type is 'refresh'");
    }
    var user =
        userRepository
            .findByUsername(usernameFromToken)
            .orElseThrow(
                () ->
                    new NoSuchObjectException(
                        "There is no user with such username: " + usernameFromToken));

    return new TokenResponse(
        generateTokenByUsername(
            user.getUsername(), roleService.getAllUserRoles(user), ACCESS_TOKEN_TYPE),
        generateTokenByUsername(
            user.getUsername(), roleService.getAllUserRoles(user), REFRESH_TOKEN_TYPE));
  }

  private String generateTokenByUsername(String username, String authorities, String tokenType) {
    return ACCESS_TOKEN_TYPE.equals(tokenType)
        ? jwtUtils.generateToken(username, authorities, tokenType, jwtTokenExpirationMs)
        : jwtUtils.generateToken(username, authorities, tokenType, jwtRefreshTokenExpirationMs);
  }

  private String generateTokenByAuthentication(Authentication authentication, String tokenType) {
    return ACCESS_TOKEN_TYPE.equals(tokenType)
        ? jwtUtils.generateToken(authentication, tokenType, jwtTokenExpirationMs)
        : jwtUtils.generateToken(authentication, tokenType, jwtRefreshTokenExpirationMs);
  }
}
