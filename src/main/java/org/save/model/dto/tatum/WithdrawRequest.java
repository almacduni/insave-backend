package org.save.model.dto.tatum;

import lombok.Data;

@Data
public class WithdrawRequest {

  private Long userId;
  private String amount;
  private String address;
}
