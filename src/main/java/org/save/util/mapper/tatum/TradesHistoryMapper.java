package org.save.util.mapper.tatum;

import org.save.model.dto.tatum.TradeHistoryDto;
import org.save.model.dto.tatum.TradeOperation;
import org.save.model.enums.tatum.OperationType;
import org.springframework.stereotype.Component;

@Component
public class TradesHistoryMapper {

  public TradeOperation convertToTradeOperation(TradeHistoryDto trade) {
    return new TradeOperation(
        trade.getCreated(),
        OperationType.TRADE,
        trade.getType(),
        trade.getPair(),
        trade.getIsMarket(),
        trade.getFill());
  }
}
