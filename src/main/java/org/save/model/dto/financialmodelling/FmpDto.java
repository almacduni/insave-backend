package org.save.model.dto.financialmodelling;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FmpDto {
  @JsonProperty("currentRatioTTM")
  private BigDecimal currentRatio;

  @JsonProperty("debtToEquityTTM")
  private BigDecimal leverageRatio;
}
