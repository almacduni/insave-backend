package org.save.config;

import org.save.service.implementation.UserDetailsServiceImpl;
import org.save.util.jwt.AuthEntryPointJwt;
import org.save.util.jwt.TokenAuthorizationExceptionFilter;
import org.save.util.jwt.TokenAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

  private static final String[] AUTH_WHITELIST_POST_REQUESTS = {
    "/api/auth/send-activation-code",
    "/api/auth/send-security-code",
    "/api/auth/sign-in",
    "/api/auth/sign-up",
    "/api/auth/token/refresh",
    "/api/auth/email/check",
    "/api/auth/username/check",
    "/user/referralLink/check",
    "/crypto/transaction",
    "/crypto/order"
  };

  private static final String[] AUTH_WHITELIST_DELETE_REQUESTS = {"/account"};

  private static final String[] AUTH_WHITELIST_PATCH_REQUESTS = {"/account"};

  private static final String[] AUTH_WHITELIST_GET_REQUESTS = {
    "/search",
    "/swagger-ui.html",
    "/swagger-ui/**",
    "/v2/api-docs",
    "/webjars/springfox-swagger-ui/**",
    "/swagger-resources",
    "/swagger-resources/**",
    "/api/auth/{username}/email",
    "/companies/search",
    "/companies/{ticker}",
    "/companies/{ticker}/description",
    "/watchlist/getUserWatchlist",
    "/posts/forYou",
    "/posts/latest",
    "/posts/statistic",
    "/posts/trending",
    "/posts/{postId}",
    "/posts/{postId}/comments",
    "/playlists",
    "/playlists/{id}",
    "/playlists/category",
    "/playlists/categories",
    "/market/stock-candles-intraday-daily",
    "/market/indicators/**"
  };

  @Autowired private UserDetailsServiceImpl userDetailsService;

  @Autowired private AuthEntryPointJwt unauthorizedHandler;

  @Autowired private TokenAuthorizationFilter tokenAuthorizationFilter;

  @Autowired private TokenAuthorizationExceptionFilter tokenAuthorizationExceptionFilter;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors()
        .and()
        .csrf()
        .disable()
        .exceptionHandling()
        .authenticationEntryPoint(unauthorizedHandler)
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers(HttpMethod.OPTIONS, "/**")
        .permitAll()
        .antMatchers(HttpMethod.POST, AUTH_WHITELIST_POST_REQUESTS)
        .permitAll()
        .antMatchers(HttpMethod.DELETE, AUTH_WHITELIST_DELETE_REQUESTS)
        .permitAll()
        .antMatchers(HttpMethod.PATCH, AUTH_WHITELIST_PATCH_REQUESTS)
        .permitAll()
        .antMatchers(HttpMethod.GET, AUTH_WHITELIST_GET_REQUESTS)
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .addFilterBefore(tokenAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(tokenAuthorizationExceptionFilter, TokenAuthorizationFilter.class);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
  }
}
