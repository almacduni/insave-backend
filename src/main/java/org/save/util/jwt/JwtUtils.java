package org.save.util.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.save.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class JwtUtils {

  public static final String AUTHORITIES_KEY = "roles";

  @Value("${insave.user-management.jwt.jwtSecret}")
  private String jwtSecret;

  public String generateToken(
      Authentication authentication, String tokenType, int jwtTokenExpirationMs) {
    return createToken(authentication, tokenType, jwtTokenExpirationMs);
  }

  public String generateToken(
      String username, String authorities, String tokenType, int jwtTokenExpirationMs) {
    return createToken(username, tokenType, jwtTokenExpirationMs, authorities);
  }

  public String getUsernameFromToken(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  public String getTokenType(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getAudience();
  }

  public void validateJwtToken(String token) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
    } catch (SignatureException
        | MalformedJwtException
        | UnsupportedJwtException
        | IllegalArgumentException e) {
      log.error(e.getMessage());
      throw new InvalidTokenException(e.getMessage());
    } catch (ExpiredJwtException e) {
      log.error(e.getMessage());
      throw new InvalidTokenException("JWT is expired");
    }
  }

  public String extractJwtToken(String authorizationHeader) {
    if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer")) {
      return authorizationHeader.substring(7);
    }
    return null;
  }

  private String createToken(
      String username, String tokenType, int tokenExpirationMs, String authorities) {
    return Jwts.builder()
        .setIssuedAt(new Date())
        .setExpiration(new Date(new Date().getTime() + tokenExpirationMs))
        .setSubject(username)
        .claim(AUTHORITIES_KEY, authorities)
        .setAudience(tokenType)
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

  private String createToken(
      Authentication authentication, String tokenType, int tokenExpirationMs) {
    var authorities =
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
    return Jwts.builder()
        .setSubject(authentication.getName())
        .claim(AUTHORITIES_KEY, authorities)
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .setIssuedAt(new Date())
        .setAudience(tokenType)
        .setExpiration(new Date(new Date().getTime() + tokenExpirationMs))
        .compact();
  }
}
