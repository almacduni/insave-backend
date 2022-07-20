package org.save.model.dto.tatum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockedAmountsResponse {

  private String id;
  private String accountId;
  private String amount;
  private String type;
  private String description;
}
