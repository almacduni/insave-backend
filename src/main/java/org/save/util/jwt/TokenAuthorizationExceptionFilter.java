package org.save.util.jwt;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.save.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * <code>TokenAuthorizationExceptionFilter</code> is used for handling exceptions during JWT
 * validation in {@link org.save.util.jwt.TokenAuthorizationFilter TokenAuthorizationFilter}
 */
@Component
public class TokenAuthorizationExceptionFilter extends OncePerRequestFilter {

  @Autowired private HandlerExceptionResolver handlerExceptionResolver;

  @Override
  @SneakyThrows
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
    try {
      filterChain.doFilter(request, response);

    } catch (InvalidTokenException e) {
      handlerExceptionResolver.resolveException(request, response, null, e);
    }
  }
}
