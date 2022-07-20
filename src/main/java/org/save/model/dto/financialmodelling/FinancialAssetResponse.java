package org.save.model.dto.financialmodelling;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinancialAssetResponse {

  private String name;
  private String ticker;
  private BigDecimal averagePrice;
  private BigDecimal amount;
  private String logoUrl;
  private BigDecimal change;
  private BigDecimal changesPercentage;
  private BigDecimal totalPrice;
}
