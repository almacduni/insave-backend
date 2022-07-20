package org.save.model.dto.financialmodelling;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EarningSurpriseDto {
  private String date;
  private String ticker;
  private BigDecimal actualEarningResult;
  private BigDecimal estimatedEarning;
}
