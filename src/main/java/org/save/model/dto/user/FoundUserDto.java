package org.save.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoundUserDto {

  private Long id;
  private String username;
  private String email;
  private String avatar;
}
