package org.save.model.dto.user;

import javax.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.save.validation.annotations.NullOrNotBlank;

@Data
public class BioAndUsernameChangeRequest {

  @Length(max = 140, message = "{bio.not.valid}")
  @NullOrNotBlank(message = "{bio.not.valid}")
  private String bio;

  @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "{username.not.valid}")
  @Length(max = 20, message = "{username.not.valid}")
  private String username;
}
