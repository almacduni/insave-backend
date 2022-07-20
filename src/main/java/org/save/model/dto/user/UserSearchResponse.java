package org.save.model.dto.user;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchResponse {

  private List<FoundUserDto> foundUsers;
  private Long total;
}
