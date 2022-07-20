package org.save.service.implementation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.save.exception.NoSuchObjectException;
import org.save.model.entity.common.RoleEntity;
import org.save.model.entity.common.User;
import org.save.model.enums.RoleEnum;
import org.save.repo.RoleRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

  private final RoleRepository roleRepository;

  public RoleService(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  public String getAllUserRoles(User user) {
    var userRoles =
        user.getRoleEntitySet().stream()
            .map(authority -> authority.getName().name())
            .collect(Collectors.joining(","));
    if (userRoles.isEmpty()) {
      throw new NoSuchObjectException("User doesn't have any role");
    }
    return userRoles;
  }

  public Collection<? extends GrantedAuthority> getUserAuthorities(User user) {
    List<SimpleGrantedAuthority> simpleGrantedAuthorityList = new ArrayList<>();
    user.getRoleEntitySet().stream()
        .map(authority -> authority.getName().name())
        .forEach(role -> simpleGrantedAuthorityList.add(new SimpleGrantedAuthority(role)));
    if (simpleGrantedAuthorityList.isEmpty()) {
      throw new NoSuchObjectException("User doesn't have any role");
    }
    return simpleGrantedAuthorityList;
  }

  public RoleEntity getRoleByName(RoleEnum roleName) {
    return roleRepository.findRoleEntityByName(roleName);
  }
}
