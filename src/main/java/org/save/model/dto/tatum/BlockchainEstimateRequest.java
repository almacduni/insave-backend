package org.save.model.dto.tatum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlockchainEstimateRequest {

  private String senderAccountId;
  private String address;
  private String amount;
}
