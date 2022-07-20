package org.save.service.implementation;

import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.save.client.ImageKitClient;
import org.save.exception.EmailIsInUseException;
import org.save.exception.IncorrectPasswordException;
import org.save.exception.InvalidAvatarFileTypeException;
import org.save.exception.InvalidLinkException;
import org.save.exception.MobileAppPasswordNotMatchesException;
import org.save.exception.NoSuchObjectException;
import org.save.exception.NotUniqueUsernameException;
import org.save.exception.PasswordIsInUseException;
import org.save.model.dto.auth.CheckPasswordRequest;
import org.save.model.dto.auth.CurrentUserResponse;
import org.save.model.dto.auth.NewEmailRequest;
import org.save.model.dto.auth.NewPasswordRequest;
import org.save.model.dto.user.FoundUserDto;
import org.save.model.dto.user.PersonalDataRequest;
import org.save.model.dto.user.ReferralLinkDto;
import org.save.model.dto.user.UserSearchResponse;
import org.save.model.entity.common.RoleEntity;
import org.save.model.entity.common.User;
import org.save.model.entity.social.picture.Picture;
import org.save.model.entity.user.PasswordRestoreLink;
import org.save.model.entity.user.PersonalData;
import org.save.model.enums.DocumentType;
import org.save.model.enums.RoleEnum;
import org.save.model.enums.VerificationStep;
import org.save.repo.PasswordRestoreLinkRepository;
import org.save.repo.PersonalDataRepository;
import org.save.repo.RoleRepository;
import org.save.repo.UserRepository;
import org.save.util.jwt.JwtUtils;
import org.save.util.picture.PictureCreator;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PersonalDataRepository personalDataRepository;
  private final PasswordRestoreLinkRepository passwordRestoreLinkRepository;
  private final PasswordEncoder passwordEncoder;
  private final MailSender mailSender;
  private final ImageKitClient imageKitClient;
  private final JwtUtils jwtUtils;

  // TODO change the way od storing to Redis
  private final Map<String, String> emailUpdateVerificationCodeMap = new HashMap<>();

  public boolean existsUserByUsername(String username) {
    return userRepository.existsUserByUsername(username);
  }

  public boolean existsUserByEmail(String email) {
    return userRepository.existsUserByEmail(email);
  }

  public boolean existsUserByReferralLink(String referralLink) {
    return userRepository.existsUserByReferralLink(referralLink);
  }

  public void changeReferralLink(Principal principal, ReferralLinkDto referralLinkDto) {
    val user = getUserByUsername(principal.getName());
    if (userRepository.existsByReferralLink(referralLinkDto.getLink())) {
      throw new InvalidLinkException("Current link already exists in the system");
    }
    user.setReferralLink(referralLinkDto.getLink());
    userRepository.save(user);
  }

  public boolean isUserInBlacklistByRefLink(String referralLink) {
    RoleEntity roleEntity = roleRepository.findRoleEntityByName(RoleEnum.ROLE_BLACKLIST);
    return userRepository.existsByReferralLinkAndRoleEntitySetIn(
        referralLink, new HashSet<>(List.of(roleEntity)));
  }

  public User findUserByReferralLink(String referralLink) {
    return userRepository.findUserByReferralLink(referralLink);
  }

  public User saveUser(User user) {
    return userRepository.saveAndFlush(user);
  }

  public User getUserById(Long id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new NoSuchObjectException("User with provided Id wasn't found"));
  }

  public User getUserByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new NoSuchObjectException("There is no user with such username"));
  }

  public Optional<User> getUserByEmail(String email) {
    return userRepository.findUserByEmail(email);
  }

  public void savePersonalData(
      PersonalDataRequest personalDataRequest, DocumentType documentType, Principal principal) {
    User user =
        userRepository
            .findByUsername(principal.getName())
            .orElseThrow(() -> new NoSuchObjectException("User wasn't found"));

    user.setVerified(VerificationStep.PENDING);

    PersonalData personalData = new PersonalData();
    personalData.setUser(user);
    personalData.setStreetAddress(personalDataRequest.getStreetAddress());
    personalData.setPostCode(personalDataRequest.getPostcode());
    personalData.setCity(personalDataRequest.getCity());
    personalData.setDate(personalDataRequest.getDate());
    personalData.setFirstName(personalDataRequest.getFirstName());
    personalData.setLastName(personalDataRequest.getLastName());
    personalData.setDocumentType(documentType);

    // TODO Store files to ImageKit
    if (personalDataRequest.getPictures() != null) {
      List<Picture> pictureList = PictureCreator.createPictures(personalDataRequest.getPictures());
      personalData.setPictures(pictureList);
    }

    userRepository.save(user);
    personalDataRepository.save(personalData);
  }

  public void checkPassword(CheckPasswordRequest checkPasswordRequest, Long userId) {
    User user = userRepository.getUserById(userId);
    if (!passwordEncoder.matches(checkPasswordRequest.getPassword(), user.getPassword())) {
      throw new IncorrectPasswordException(
          "Password must be between 8 and 64, at least 1 uppercase character and 1 number, no whitespace allowed");
    }
  }

  public CurrentUserResponse getCurrentUser(String authorizationHeader) {
    String token = jwtUtils.extractJwtToken(authorizationHeader);
    String username = jwtUtils.getUsernameFromToken(token);
    return userRepository
        .findUserByUsername(username)
        .orElseThrow(() -> new NoSuchObjectException("There is no user with such username"));
  }

  public User updatePassword(NewPasswordRequest newPasswordRequest, Long userId) {
    User user = userRepository.getUserById(userId);
    if (passwordEncoder.matches(newPasswordRequest.getPassword(), user.getPassword())) {
      throw new PasswordIsInUseException("Password is already in use");
    }
    user.setPassword(passwordEncoder.encode(newPasswordRequest.getPassword()));
    return userRepository.save(user);
  }

  public String sendEmailChangeCode(Long userId, NewEmailRequest emailRequest) {
    String newEmail = emailRequest.getEmail();
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NoSuchObjectException("there is no such user"));

    if (user.getEmail().equals(newEmail)) {
      throw new EmailIsInUseException("Email is already in use");
    }
    if (userRepository.existsUserByEmail(newEmail)) {
      throw new EmailIsInUseException("Email is already in use");
    }

    final int securityCode = 100000 + ThreadLocalRandom.current().nextInt(900000);
    emailUpdateVerificationCodeMap.put(String.valueOf(securityCode), newEmail);

    mailSender.send(newEmail, "Security code", String.valueOf(securityCode));

    return newEmail;
  }

  public String updateEmail(Long userId, Integer code) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> new NoSuchObjectException("User with id = " + userId + " not found"));
    String email =
        Optional.ofNullable(emailUpdateVerificationCodeMap.get(String.valueOf(code)))
            .orElseThrow(() -> new NoSuchObjectException("code is incorrect"));

    if (userRepository.existsUserByEmail(email)) {
      throw new EmailIsInUseException("Email is already in use");
    }
    emailUpdateVerificationCodeMap.remove(String.valueOf(code));

    user.setEmail(email);
    userRepository.save(user);

    return email;
  }

  public void updateBioAndUsername(String bio, String username, Principal principal) {
    Long userId = userRepository.getUerIdByUsername(principal.getName());
    User user = userRepository.getUserByUserId(userId);

    if (userRepository.existsUserByUsername(username) && !user.getUsername().equals(username)) {
      throw new NotUniqueUsernameException(
          "This username is already in use. Please, choose unique username");
    }

    if (!Objects.equals(bio, null) && !Objects.equals(username, null)) {
      userRepository.updateBioAndUsername(username, bio, userId);
    } else if (Objects.equals(bio, null) && !Objects.equals(username, null)) {
      userRepository.updateUsername(username, userId);
    } else if (!Objects.equals(bio, null)) {
      userRepository.updateBio(bio, userId);
    }
  }

  public void setPasswordForMobileApp(Long id, String password) {
    String newPassword = passwordEncoder.encode(password);
    userRepository.setPasswordForMobileApp(newPassword, id);
  }

  public void confirmMobileAppPassword(String password, long id) {
    User user = getUserById(id);
    boolean ifMatches = passwordEncoder.matches(password, user.getPasswordOnMobileApp());
    if (!ifMatches) {
      throw new MobileAppPasswordNotMatchesException("Invalid Password");
    }
  }

  public UserSearchResponse searchUserByUsernameOrEmail(
      String searchString, Long offset, Long limit) {
    String searchStringParameter = searchString + "%";
    List<FoundUserDto> foundUsers =
        userRepository.searchUserByUsernameOrEmail(searchStringParameter, offset, limit).stream()
            .map(
                user ->
                    new FoundUserDto(
                        user.getId(), user.getUsername(), user.getEmail(), user.getAvatarLink()))
            .collect(Collectors.toList());
    Long total = userRepository.getFoundUsersCount(searchStringParameter);
    return new UserSearchResponse(foundUsers, total);
  }

  public void createLinkForPasswordRestore(String email) {
    User user =
        userRepository
            .findUserByEmail(email)
            .orElseThrow(() -> new NoSuchObjectException("There is no user with such email"));

    UUID id = UUID.randomUUID();
    String link = "https://insave.io/password/reset/" + id;

    PasswordRestoreLink passwordRestoreLink = new PasswordRestoreLink();
    passwordRestoreLink.setId(id);
    passwordRestoreLink.setUserId(user.getId());
    passwordRestoreLinkRepository.save(passwordRestoreLink);

    mailSender.send(email, "Password Restore Link", link);
  }

  public Boolean checkLinkForPasswordRestore(UUID id) {
    boolean ifLinkExists = passwordRestoreLinkRepository.existsById(id);
    if (!ifLinkExists) {
      throw new InvalidLinkException("Invalid password restore link");
    }
    return true;
  }

  public void resetPassword(UUID id, NewPasswordRequest password) {
    PasswordRestoreLink forgotPassword =
        passwordRestoreLinkRepository
            .findById(id)
            .orElseThrow(() -> new InvalidLinkException("Invalid password restore link"));

    User user = getUserById(forgotPassword.getUserId());
    String newPassword = passwordEncoder.encode(password.getPassword());
    userRepository.changePassword(newPassword, user.getId());
    passwordRestoreLinkRepository.deleteById(id);
  }

  public String uploadAvatar(MultipartFile file, Principal principal) {
    if (!file.getContentType().startsWith("image")) {
      throw new InvalidAvatarFileTypeException("File type must be image");
    }
    Resource resource = file.getResource();
    Long userId = userRepository.getUerIdByUsername(principal.getName());
    User user = userRepository.getUserByUserId(userId);
    String fileName = "user_avatar_" + userId;
    String filePath = "users/" + userId + "/avatar/";
    String avatarLink = imageKitClient.uploadFileToStorage(resource, fileName, filePath, false);
    userRepository.updateAvatar(avatarLink, user.getId());
    return avatarLink;
  }
}
