package org.save.model.dto.user.secure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.save.model.entity.common.RoleEntity;
import org.save.model.entity.common.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@EqualsAndHashCode
public class UserDetailsImpl implements UserDetails {

  @Getter private Long id;
  private String username;

  @Getter private String firstName;

  @Getter private String lastName;

  @Getter private String email;

  @Getter private String verified;

  @Getter private String bio;

  @JsonIgnore private String password;

  @Getter private Set<RoleEntity> roles;

  @Getter private String referralLink;
  private Collection<? extends GrantedAuthority> authorities;

  public UserDetailsImpl(
      Long id,
      String username,
      String email,
      String password,
      String referralLink,
      String verified,
      String bio,
      Set<RoleEntity> roles) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
    this.referralLink = referralLink;
    this.verified = verified;
    this.bio = bio;
    this.roles = roles;
  }

  public static UserDetailsImpl build(User user) {
    return new UserDetailsImpl(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getPassword(),
        user.getReferralLink(),
        user.getVerified().getVerificationStep(),
        user.getBio(),
        user.getRoleEntitySet());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    var roleEntitySet = getRoles();
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();

    for (RoleEntity role : roleEntitySet) {
      authorities.add(new SimpleGrantedAuthority(role.getName().name()));
    }

    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
