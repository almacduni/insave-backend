package org.save.util.mapper.tatum;

import org.save.model.dto.tatum.TransactionDto;
import org.save.model.dto.tatum.TransactionOperation;
import org.springframework.stereotype.Component;

@Component
public class TransactionHistoryMapper {

  public TransactionOperation convertToAccountOperation(TransactionDto transaction) {
    return new TransactionOperation(
        transaction.getCreated(),
        transaction.getOperationType(),
        transaction.getCurrency(),
        transaction.getAmount());
  }
}
