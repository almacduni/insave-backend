package org.save.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.save.model.dto.auth.CheckUsernameRequest;
import org.save.model.dto.auth.EmailRequest;
import org.save.model.dto.auth.SignInRequest;
import org.save.model.dto.auth.SignUpRequest;
import org.save.model.dto.auth.TokenResponse;
import org.save.service.implementation.AuthorizationService;
import org.save.service.implementation.JwtTokenService;
import org.save.service.implementation.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthorizationController {

  private final AuthorizationService authorizationService;
  private final JwtTokenService jwtTokenService;
  private final UserService userService;

  @PostMapping("/sign-in")
  public ResponseEntity<TokenResponse> signIn(
      @Valid @RequestBody SignInRequest signInRequest, @RequestParam Integer securityCode) {

    log.info(
        "POST: /api/auth/sign-in securityCode={} signInRequest={}", securityCode, signInRequest);

    return new ResponseEntity<>(
        authorizationService.signInUser(signInRequest, securityCode), HttpStatus.OK);
  }

  @PostMapping("/sign-up")
  public ResponseEntity<TokenResponse> signUp(
      @Valid @RequestBody SignUpRequest signUpRequest,
      @RequestParam Integer activationCode,
      @RequestParam(required = false) String parentReferralLink) {

    log.info(
        "POST: /api/auth/sign-up activationCode={} signUpRequest={} parentReferralLink={}",
        activationCode,
        signUpRequest,
        parentReferralLink);

    return new ResponseEntity<>(
        authorizationService.signUpUser(signUpRequest, activationCode, parentReferralLink),
        HttpStatus.OK);
  }

  @PostMapping("/send-activation-code")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void sendActivationCode(@Valid @RequestBody SignUpRequest signUpRequest) {

    log.info("POST: /api/auth/send-activation-code signUpRequest={}", signUpRequest);
    authorizationService.sendActivationCode(signUpRequest);
  }

  @PostMapping("/send-security-code")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void sendSecurityCode(@Valid @RequestBody SignInRequest signInRequest) {

    log.info("POST: /api/auth/send-security-code signInRequest={}", signInRequest);
    authorizationService.sendSecurityCode(signInRequest);
  }

  @PostMapping("/email/check")
  public ResponseEntity<Boolean> checkEmail(@Valid @RequestBody EmailRequest emailRequest) {

    log.info("POST: /api/auth/email/check email={}", emailRequest.getEmail());

    return new ResponseEntity<>(
        userService.existsUserByEmail(emailRequest.getEmail()), HttpStatus.OK);
  }

  @PostMapping("/username/check")
  public ResponseEntity<Boolean> checkUsername(
      @Valid @RequestBody CheckUsernameRequest checkUsernameRequest) {

    log.info("POST: /api/auth/username/check username={}", checkUsernameRequest.getUsername());

    return new ResponseEntity<>(
        userService.existsUserByUsername(checkUsernameRequest.getUsername()), HttpStatus.OK);
  }

  @GetMapping("/{username}/email")
  public ResponseEntity<String> getEmail(@PathVariable String username) {
    String response = userService.getUserByUsername(username).getEmail();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/token/refresh")
  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  public ResponseEntity<TokenResponse> generateNewTokens(
      @RequestHeader("Authorization") String refreshToken) {
    TokenResponse tokenResponse = jwtTokenService.generateNewTokens(refreshToken);
    return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
  }
}
