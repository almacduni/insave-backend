package org.save.model.entity.user;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "password_restore_links")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordRestoreLink {

  @Id private UUID id;

  private Long userId;
}
