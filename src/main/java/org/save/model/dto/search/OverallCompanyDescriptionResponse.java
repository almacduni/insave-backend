package org.save.model.dto.search;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.save.model.dto.financialmodelling.CompanyDescription;
import org.save.model.dto.financialmodelling.EarningSurpriseDto;
import org.save.model.dto.financialmodelling.FinancialStatementDto;
import org.save.model.dto.playlist.ExplorePlaylistResponse;

@Data
@AllArgsConstructor
public class OverallCompanyDescriptionResponse {

  private CompanyDescription companyDescription;
  private List<FinancialStatementDto> financialStatement;
  private List<EarningSurpriseDto> earningSurprise;
  private List<ExplorePlaylistResponse> companyDescriptionPlaylists;
}
