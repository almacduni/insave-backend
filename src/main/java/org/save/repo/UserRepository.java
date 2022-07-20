package org.save.repo;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.save.model.dto.auth.CurrentUserResponse;
import org.save.model.entity.common.RoleEntity;
import org.save.model.entity.common.User;
import org.save.model.enums.AccountStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @Query(value = "SELECT user_id FROM users WHERE users.username =:username", nativeQuery = true)
  Long getUerIdByUsername(@Param("username") String username);

  User getUserById(Long id);

  List<User> findAllByAccountStatus(AccountStatusEnum accountStatusEnum);

  @Query(value = "SELECT * FROM users WHERE users.user_id = :userId", nativeQuery = true)
  User getUserByUserId(Long userId);

  Optional<User> findByUsername(String username);

  Optional<User> findUserByEmail(String email);

  boolean existsUserByUsername(String username);

  boolean existsUserByEmail(String email);

  void deleteById(Long id);

  Optional<CurrentUserResponse> findUserByUsername(String username);

  boolean existsUserByReferralLink(String referralLink);

  boolean existsByReferralLinkAndRoleEntitySetIn(String referralLink, Set<RoleEntity> entitySet);

  @Query(
      value = "SELECT user_id FROM users WHERE users.referral_link = :referralLink",
      nativeQuery = true)
  Long findUserIdByReferralLink(String referralLink);

  User findUserByReferralLink(String referralLink);

  @Query(value = "SELECT COUNT(*) FROM users " + "WHERE parent_id = :parentId ", nativeQuery = true)
  Long getCountInvitedFriends(@Param("parentId") Long parentId);

  boolean existsByReferralLink(String referralLink);

  @Modifying
  @Transactional
  @Query(value = "UPDATE users SET bio = :bio WHERE user_id = :userId", nativeQuery = true)
  void updateBio(String bio, Long userId);

  @Modifying
  @Transactional
  @Query(
      value = "UPDATE users SET username = :username WHERE user_id = :userId",
      nativeQuery = true)
  void updateUsername(String username, Long userId);

  @Modifying
  @Transactional
  @Query(
      value = "UPDATE users SET username = :username, bio = :bio WHERE user_id = :userId",
      nativeQuery = true)
  void updateBioAndUsername(String username, String bio, Long userId);

  @Modifying
  @Transactional
  @Query(
      value = "UPDATE users SET password_on_mobile_app = :password WHERE user_id = :userId",
      nativeQuery = true)
  void setPasswordForMobileApp(String password, Long userId);

  @Modifying
  @Transactional
  @Query(
      value = "UPDATE users SET avatar_link = :avatarLink WHERE user_id = :userId",
      nativeQuery = true)
  void updateAvatar(String avatarLink, Long userId);

  @Modifying
  @Transactional
  @Query(
      value = "UPDATE users SET password = :password WHERE user_id = :userId",
      nativeQuery = true)
  void changePassword(String password, Long userId);

  @Modifying
  @Transactional
  @Query(
      value =
          "SELECT * FROM users WHERE "
              + "username LIKE :search_string OR "
              + "email LIKE :search_string "
              + "ORDER BY user_id ASC "
              + "offset :offset limit :limit",
      nativeQuery = true)
  List<User> searchUserByUsernameOrEmail(
      @Param("search_string") String searchString,
      @Param("offset") Long offset,
      @Param("limit") Long limit);

  List<User> findAllByUsername(String username);

  @Query(
      value =
          "SELECT COUNT(*) FROM users WHERE "
              + "username LIKE :search_string OR "
              + "email LIKE :search_string",
      nativeQuery = true)
  Long getFoundUsersCount(@Param("search_string") String searchString);
}
