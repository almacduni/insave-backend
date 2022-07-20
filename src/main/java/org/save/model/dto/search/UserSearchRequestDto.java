package org.save.model.dto.search;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserSearchRequestDto extends CommonSearchRequestDto {

  private String username;
  private String avatarLink;
}
