package org.save.model.dto.tatum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.save.model.enums.tatum.OperationType;
import org.save.model.enums.tatum.TransactionType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

  private String accountId;
  private String counterAccountId;
  private String currency;
  private String amount;
  private Boolean anonymous;
  private Long created;
  private MarketValue marketValue;
  private OperationType operationType;
  private TransactionType transactionType;
  private String reference;
  private String transactionCode;
  private String senderNote;
  private String recipientNote;
  private String paymentId;
  private String attr;
  private String address;
  private String txId;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  private static class MarketValue {

    String amount;
    String currency;
    Long sourceDate;
    String source;
  }
}
