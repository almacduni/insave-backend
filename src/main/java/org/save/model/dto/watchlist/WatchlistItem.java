package org.save.model.dto.watchlist;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.save.model.enums.TradingStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistItem {

  private String name;

  @JsonInclude(Include.NON_NULL)
  private String description;

  @JsonProperty("symbol")
  private String ticker;

  @JsonProperty("image")
  private String logo;

  private BigDecimal change;
  private BigDecimal changesPercentage;

  @JsonProperty("exchange")
  private String itemType;

  private BigDecimal price;
  private TradingStatus tradingStatus;
  private boolean isMarketDataSupported;
}
