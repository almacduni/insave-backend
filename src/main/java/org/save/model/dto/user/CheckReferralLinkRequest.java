package org.save.model.dto.user;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckReferralLinkRequest {

  @NotNull
  @Size(min = 8, max = 8)
  private String referralLink;
}
