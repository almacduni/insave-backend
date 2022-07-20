package org.save.repo;

import org.save.model.entity.common.RoleEntity;
import org.save.model.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

  RoleEntity findRoleEntityByName(RoleEnum name);
}
