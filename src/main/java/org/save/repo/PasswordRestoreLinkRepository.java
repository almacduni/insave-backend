package org.save.repo;

import java.util.UUID;
import org.save.model.entity.user.PasswordRestoreLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRestoreLinkRepository extends JpaRepository<PasswordRestoreLink, UUID> {}
