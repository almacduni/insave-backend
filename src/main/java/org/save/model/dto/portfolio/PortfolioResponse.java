package org.save.model.dto.portfolio;

import java.util.List;
import lombok.Data;
import org.save.model.dto.financialmodelling.FinancialAssetResponse;
import org.save.model.entity.common.Performance;

@Data
public class PortfolioResponse {

  private Performance performance;
  private List<FinancialAssetResponse> financialAssets;
}
