package org.save.model.dto.ticker;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.save.model.enums.TradingStatus;

@Data
@AllArgsConstructor
public class TickerStatusRequest {

  @NotBlank private String ticker;
  private TradingStatus tradingStatus;
  private boolean isMarketDataSupported;
}
