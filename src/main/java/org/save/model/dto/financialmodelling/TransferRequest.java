package org.save.model.dto.financialmodelling;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.save.model.enums.CryptoCurrency;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {

  private Long senderId;
  private Long recipientId;
  private String amount;
  private CryptoCurrency cryptoCurrency;
}
