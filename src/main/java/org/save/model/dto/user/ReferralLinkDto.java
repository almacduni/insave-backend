package org.save.model.dto.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class ReferralLinkDto {

  @NotBlank
  @Size(min = 1, max = 30)
  private String link;
}
