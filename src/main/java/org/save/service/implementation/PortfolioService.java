package org.save.service.implementation;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.save.client.FinancialModellingClient;
import org.save.model.dto.financialmodelling.FinancialAssetResponse;
import org.save.model.dto.portfolio.PortfolioResponse;
import org.save.model.dto.search.GeneralInfoInSearchDto;
import org.save.model.entity.common.Performance;
import org.save.model.entity.common.Portfolio;
import org.save.model.entity.common.User;
import org.save.repo.PortfolioRepository;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PortfolioService {

  private final FinancialModellingClient financialModellingClient;
  private final AssetService assetService;
  private final PortfolioRepository portfolioRepository;
  private final PerformanceService performanceService;

  public Portfolio createPortfolio(User user) {
    Portfolio portfolio = new Portfolio();
    portfolio.setPerformance(new Performance());
    portfolio.setUser(user);
    return portfolioRepository.save(portfolio);
  }

  public PortfolioResponse getPortfolio(Long userId) {
    PortfolioResponse portfolioResponse = new PortfolioResponse();

    Portfolio portfolio = portfolioRepository.getPortfolioByUserId(userId);
    assetService.updateCryptoAssets(portfolio);

    performanceService.updatePerformanceValues(portfolio);

    List<FinancialAssetResponse> assets =
        portfolio.getAssets().stream()
            .map(
                asset -> {
                  GeneralInfoInSearchDto companyInfo =
                      financialModellingClient.getCompanyInfo(asset.getTicker());
                  return new FinancialAssetResponse(
                      asset.getCompanyName(),
                      asset.getTicker(),
                      asset.getAveragePrice(),
                      asset.getAmount(),
                      asset.getLogoUrl(),
                      companyInfo.getChange(),
                      companyInfo.getChangesPercentage(),
                      companyInfo.getPrice().multiply(asset.getAmount()));
                })
            .filter(asset -> asset.getAmount().doubleValue() > 0)
            .collect(Collectors.toList());
    portfolioResponse.setFinancialAssets(assets);
    portfolioResponse.setPerformance(portfolio.getPerformance());
    return portfolioResponse;
  }
}
