package org.save.model.dto.polygon;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloseStockDto {

  @JsonProperty("date")
  private String date;

  @JsonProperty("close")
  private BigDecimal close;
}
