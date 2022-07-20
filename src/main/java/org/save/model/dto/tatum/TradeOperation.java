package org.save.model.dto.tatum;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.save.model.enums.tatum.HistoricalTradeType;
import org.save.model.enums.tatum.OperationType;

@Data
@NoArgsConstructor
public class TradeOperation extends AccountOperation {

  private HistoricalTradeType tradeType;
  private String pair;
  private Boolean isMaker;
  private String fill;

  public TradeOperation(
      Long time,
      OperationType operationType,
      HistoricalTradeType tradeType,
      String pair,
      Boolean isMaker,
      String fill) {
    super(time, operationType);
    this.tradeType = tradeType;
    this.pair = pair;
    this.isMaker = isMaker;
    this.fill = fill;
  }
}
