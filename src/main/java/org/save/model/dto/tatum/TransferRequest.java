package org.save.model.dto.tatum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {

  private String senderAccountId;
  private String recipientAccountId;
  private String amount;
  private String recipientNote;
}
