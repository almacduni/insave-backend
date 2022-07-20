package org.save.model.dto.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SignInRequest {

  @Size(min = 4, max = 20)
  private String username;

  @Size(max = 50)
  @Email
  @NotNull(message = "Email must not be null")
  private String email;

  public String getEmail() {
    return email.toLowerCase();
  }

  @NotBlank
  @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=\\S+$).{8,64}$", message = "{password.not.valid}")
  @ToString.Exclude
  private String password;
}
