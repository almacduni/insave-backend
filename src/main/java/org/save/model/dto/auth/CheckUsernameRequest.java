package org.save.model.dto.auth;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckUsernameRequest {

  @NotNull
  @Size(min = 4, max = 20)
  private String username;
}
