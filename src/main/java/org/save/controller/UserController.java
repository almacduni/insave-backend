package org.save.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.save.model.dto.auth.CheckPasswordRequest;
import org.save.model.dto.auth.CurrentUserResponse;
import org.save.model.dto.auth.EmailRequest;
import org.save.model.dto.auth.NewEmailRequest;
import org.save.model.dto.auth.NewPasswordRequest;
import org.save.model.dto.user.BioAndUsernameChangeRequest;
import org.save.model.dto.user.CheckReferralLinkRequest;
import org.save.model.dto.user.PasswordOnMobileAppRequest;
import org.save.model.dto.user.PersonalDataRequest;
import org.save.model.dto.user.ReferralLinkDto;
import org.save.model.entity.common.User;
import org.save.model.enums.DocumentType;
import org.save.service.finance.ReferralService;
import org.save.service.implementation.UserService;
import org.save.service.serach.SearchHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final SearchHistoryService searchHistoryService;
  private final ReferralService referralService;

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @GetMapping("/current")
  public CurrentUserResponse getCurrentUser(
      @RequestHeader("Authorization") String authorizationHeader) {
    return userService.getCurrentUser(authorizationHeader);
  }

  @Secured({"ROLE_USER"})
  @GetMapping("/{id}")
  public User getUser(@PathVariable Long id) {
    return userService.getUserById(id);
  }

  @Secured({"ROLE_USER"})
  @GetMapping("/search/{searchString}/{offset}/{limit}")
  public ResponseEntity<?> searchByUsernameOrEmail(
      @PathVariable("searchString") String searchString,
      @PathVariable("offset") Long offset,
      @PathVariable("limit") Long limit) {
    return new ResponseEntity<>(
        userService.searchUserByUsernameOrEmail(searchString, offset, limit), HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED"})
  @PostMapping("/personal-data")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void sendPersonalData(
      @RequestBody PersonalDataRequest personalDataRequest,
      @RequestParam DocumentType documentType,
      Principal principal) {
    userService.savePersonalData(personalDataRequest, documentType, principal);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @PostMapping("/{id}/password/check")
  public ResponseEntity<?> checkPassword(
      @PathVariable Long id, @Valid @RequestBody CheckPasswordRequest checkPasswordRequest) {
    userService.checkPassword(checkPasswordRequest, id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @PatchMapping("/{id}/password/change")
  public ResponseEntity<?> changePassword(
      @PathVariable Long id, @Valid @RequestBody NewPasswordRequest newPasswordRequest) {
    userService.updatePassword(newPasswordRequest, id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @PostMapping("/{id}/email/code")
  public ResponseEntity<String> sendEmailVerificationCode(
      @PathVariable Long id, @Valid @RequestBody NewEmailRequest emailRequest) {
    String response = userService.sendEmailChangeCode(id, emailRequest);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @PatchMapping("/{id}/{code}/email/change")
  public ResponseEntity<String> changeEmail(@PathVariable Long id, @PathVariable Integer code) {
    String response = userService.updateEmail(id, code);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @PostMapping("/bio")
  public ResponseEntity<?> changeBioAndUsername(
      @Valid @RequestBody BioAndUsernameChangeRequest bioAndUsername, Principal principal) {
    userService.updateBioAndUsername(
        bioAndUsername.getBio(), bioAndUsername.getUsername(), principal);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @PostMapping("/avatar")
  public ResponseEntity<String> changeAvatar(
      @RequestParam MultipartFile file, Principal principal) {
    String response = userService.uploadAvatar(file, principal);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @GetMapping("/{id}/history")
  public ResponseEntity<List<String>> getUserSearchHistory(@PathVariable Long id) {
    return new ResponseEntity<>(searchHistoryService.getHistory(id), HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @PostMapping("/{id}/mobileApp/password")
  public ResponseEntity<?> setPasswordForMobileApp(
      @PathVariable Long id,
      @RequestBody @Valid PasswordOnMobileAppRequest passwordOnMobileAppRequest) {
    userService.setPasswordForMobileApp(id, passwordOnMobileAppRequest.getPassword());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @PostMapping("/{id}/mobileApp/password/confirm")
  public ResponseEntity<?> confirmPasswordForMobileApp(
      @PathVariable Long id,
      @RequestBody @Valid PasswordOnMobileAppRequest passwordOnMobileAppRequest) {
    userService.confirmMobileAppPassword(passwordOnMobileAppRequest.getPassword(), id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @PostMapping("/password/restore")
  public ResponseEntity<?> createLinkForPasswordRestore(@RequestBody EmailRequest emailRequest) {
    userService.createLinkForPasswordRestore(emailRequest.getEmail());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @PostMapping("/password/restore/{id}/check")
  public ResponseEntity<Boolean> checkLinkForPasswordRestore(@PathVariable UUID id) {
    return new ResponseEntity<>(userService.checkLinkForPasswordRestore(id), HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @PostMapping("/password/restore/{id}/reset")
  public ResponseEntity<?> resetPassword(
      @PathVariable UUID id, @RequestBody @Valid NewPasswordRequest password) {
    userService.resetPassword(id, password);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED"})
  @PatchMapping("/referralLink")
  @ResponseStatus(HttpStatus.OK)
  public void changeReferralLink(Principal principal, @Valid ReferralLinkDto referralLinkDto) {
    userService.changeReferralLink(principal, referralLinkDto);
  }

  @PostMapping("/referralLink/check")
  public ResponseEntity<Boolean> checkReferralLink(
      @RequestBody CheckReferralLinkRequest checkReferralLinkRequest) {

    log.info(
        "POST: /user/referralLink/check referralLink={}",
        checkReferralLinkRequest.getReferralLink());

    return new ResponseEntity<>(
        referralService.checkReferralLinkIsValid(checkReferralLinkRequest.getReferralLink()),
        HttpStatus.OK);
  }
}
