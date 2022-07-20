package org.save.model.dto.ticker;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpToDateTickerResponse {

  @JsonProperty("price")
  private BigDecimal price;

  @JsonProperty("change")
  private BigDecimal change;

  @JsonProperty("changesPercentage")
  private BigDecimal changesPercentage;
}
