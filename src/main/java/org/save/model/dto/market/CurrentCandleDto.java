package org.save.model.dto.market;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentCandleDto {

  @JsonProperty("price")
  private BigDecimal price;

  @JsonProperty("volume")
  private BigDecimal volume;
}
