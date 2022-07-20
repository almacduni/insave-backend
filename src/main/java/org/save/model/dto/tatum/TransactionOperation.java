package org.save.model.dto.tatum;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.save.model.enums.tatum.OperationType;

@Data
@NoArgsConstructor
public class TransactionOperation extends AccountOperation {

  private String currency;
  private String amount;

  public TransactionOperation(
      Long time, OperationType operationType, String currency, String amount) {
    super(time, operationType);
    this.currency = currency;
    this.amount = amount;
  }
}
