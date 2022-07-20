package org.save.util.jwt;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class TokenAuthorizationFilter extends OncePerRequestFilter {

  private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
  private static final String ACCESS_TOKEN_TYPE = "access";
  private static final String REFRESH_TOKEN_TYPE = "refresh";
  private static final String REFRESH_TOKEN_URL = "/api/auth/token/refresh";

  @Qualifier(value = "userDetailsServiceImpl")
  private final UserDetailsService userDetailsService;

  private final JwtUtils jwtUtils;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
    String token = jwtUtils.extractJwtToken(request.getHeader(AUTHORIZATION_HEADER_NAME));
    if (StringUtils.hasText(token)) {
      jwtUtils.validateJwtToken(token);
      String tokenType = jwtUtils.getTokenType(token);
      authenticate(request, token, tokenType);
    }

    try {
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void authenticate(HttpServletRequest request, String token, String tokenType) {
    if (checkTokenType(tokenType, ACCESS_TOKEN_TYPE)
        || (checkTokenType(tokenType, REFRESH_TOKEN_TYPE)
            && request.getRequestURI().contains(REFRESH_TOKEN_URL))) {
      setAuthentication(request, token);
    }
  }

  private boolean checkTokenType(String tokenType, String accessTokenType) {
    return accessTokenType.equals(tokenType);
  }

  private void setAuthentication(HttpServletRequest request, String token) {
    var username = jwtUtils.getUsernameFromToken(token);
    var userDetails = userDetailsService.loadUserByUsername(username);
    var authentication =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
