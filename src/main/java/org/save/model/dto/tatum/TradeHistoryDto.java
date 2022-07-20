package org.save.model.dto.tatum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.save.model.enums.tatum.HistoricalTradeType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeHistoryDto {

  private String id;
  private HistoricalTradeType type;
  private String price;
  private String amount;
  private String pair;
  private Boolean isMarket;
  private String fill;
  private String feeAccountId;
  private Long fee;
  private String currency1AccountId;
  private String currency2AccountId;
  private Long created;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  private static class Attribute {

    private Long sealDate;
    private Long percentBlock;
    private Long percentPenalty;
  }
}
