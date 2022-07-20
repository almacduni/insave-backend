package org.save.model.dto.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewPasswordRequest {

  @NotBlank
  @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=\\S+$).{8,64}$", message = "{password.not.valid}")
  private String password;
}
