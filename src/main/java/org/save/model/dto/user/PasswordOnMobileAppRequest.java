package org.save.model.dto.user;

import javax.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.save.validation.annotations.NullOrNotBlank;

@Data
public class PasswordOnMobileAppRequest {

  @Pattern(regexp = "^[0-9]+$", message = "{mobile-app.password.not.valid.regexp}")
  @Length(max = 4, message = "{mobile-app.password.not.valid.length}")
  @NullOrNotBlank(message = "{mobile-app.password.not.valid.blank}")
  private String password;
}
