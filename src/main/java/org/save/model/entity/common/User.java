package org.save.model.entity.common;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.save.model.entity.social.playlist.Playlist;
import org.save.model.enums.AccountStatusEnum;
import org.save.model.enums.VerificationStep;

@Data
@Entity
@Table(
    name = "users",
    uniqueConstraints = {
      @UniqueConstraint(columnNames = "username"),
      @UniqueConstraint(columnNames = "email")
    })
@EqualsAndHashCode(exclude = {"portfolio", "wallet", "playlists"})
@ToString(exclude = {"portfolio", "wallet", "playlists"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  @Id
  @Column(name = "user_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  @NotBlank
  private String username;

  @NotBlank private String password;
  private String phone;
  private String email;

  @Column private Instant dayCreated;

  @Column private String referralLink;

  @Column private Long parentId;

  @Enumerated(EnumType.STRING)
  @Column
  private VerificationStep verified;

  @Column private String bio;

  @JsonManagedReference
  @OneToOne(cascade = CascadeType.ALL)
  private Portfolio portfolio;

  @JsonManagedReference
  @OneToOne(cascade = CascadeType.ALL)
  private Wallet wallet;

  @JsonManagedReference
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Playlist> playlists;

  @Enumerated(EnumType.STRING)
  private AccountStatusEnum accountStatus;

  private Integer expirationDays;

  private String passwordOnMobileApp;
  private String avatarLink;

  public String getEmail() {
    return email.toLowerCase();
  }

  public void setEmail(String email) {
    this.email = email.toLowerCase();
  }

  @ManyToMany(
      cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH},
      fetch = FetchType.EAGER)
  @JoinTable(
      name = "users_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<RoleEntity> roleEntitySet;

  public Integer getExpirationDays() {
    return expirationDays;
  }

  public void setExpirationDays(Integer expirationDays) {
    this.expirationDays = expirationDays;
  }

  public AccountStatusEnum getAccountStatus() {
    return accountStatus;
  }

  public void setAccountStatus(AccountStatusEnum accountStatus) {
    this.accountStatus = accountStatus;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Set<RoleEntity> getRoleEntitySet() {
    return roleEntitySet;
  }

  public void setRoleEntitySet(Set<RoleEntity> roleEntitySet) {
    this.roleEntitySet = roleEntitySet;
  }

  public User(String userName, String email, String password, String phone) {
    this.username = userName;
    this.password = password;
    this.phone = phone;
    this.email = email;
    this.dayCreated = Instant.now();
    this.verified = VerificationStep.NOT_VERIFIED;
    this.passwordOnMobileApp = null;
  }
}
