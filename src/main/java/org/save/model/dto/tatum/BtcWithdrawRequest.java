package org.save.model.dto.tatum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BtcWithdrawRequest {

  private String senderAccountId;
  private String address;
  private String amount;
  private String signatureId;
  private String xpub;
}