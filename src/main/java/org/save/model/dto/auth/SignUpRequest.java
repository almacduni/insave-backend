package org.save.model.dto.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@AllArgsConstructor
public class SignUpRequest {

  @NotBlank
  @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=\\S+$).{8,64}$", message = "{password.not.valid}")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private String password;

  @NotBlank
  @Size(max = 50)
  @Email
  @EqualsAndHashCode.Include
  private String email;

  @EqualsAndHashCode.Exclude private String phone;
}
