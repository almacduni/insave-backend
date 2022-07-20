package org.save.model.dto.tatum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlockchainEstimateResponse {

  private String fast;
  private String medium;
  private String slow;
}
