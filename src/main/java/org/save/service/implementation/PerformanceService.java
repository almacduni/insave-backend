package org.save.service.implementation;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.save.client.FinancialModellingClient;
import org.save.model.dto.portfolio.PerformanceHistoryItemDto;
import org.save.model.dto.search.GeneralInfoInSearchDto;
import org.save.model.entity.common.Asset;
import org.save.model.entity.common.Performance;
import org.save.model.entity.common.Portfolio;
import org.save.repo.AssetRepository;
import org.save.repo.PerformanceRepository;
import org.save.repo.PortfolioRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class PerformanceService {

  private static final int DAYS_OF_HISTORY = 90;

  private final PortfolioRepository portfolioRepository;
  private final FinancialModellingClient financialModellingClient;
  private final AssetRepository assetRepository;
  private final PerformanceRepository performanceRepository;
  private final DateTimeFormatter dateFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ").withZone(ZoneOffset.UTC);

  @Async
  @Scheduled(cron = "@midnight")
  public void updateInfo() {
    portfolioRepository
        .findAll()
        .forEach(
            portfolio -> {
              Performance performance = updatePerformanceValues(portfolio);
              List<PerformanceHistoryItemDto> updatedHistory =
                  updatePerformanceHistory(performance.getHistory(), performance.getValue());
              performance.setHistory(updatedHistory);
              performanceRepository.save(performance);
            });
  }

  public Performance updatePerformanceValues(Portfolio portfolio) {
    Performance performance = portfolio.getPerformance();
    List<GeneralInfoInSearchDto> assets =
        getAssets(portfolio).stream()
            .filter(asset -> asset.getPrice().compareTo(BigDecimal.ZERO) != 0)
            .collect(Collectors.toList());
    if (assets.isEmpty()) {
      return performance;
    }

    Map<String, BigDecimal> tickerPriceMap =
        assets.stream()
            .collect(
                Collectors.toMap(
                    GeneralInfoInSearchDto::getTicker, GeneralInfoInSearchDto::getPrice));

    BigDecimal performanceValue = calculatePerformanceValue(portfolio, tickerPriceMap);
    List<PerformanceHistoryItemDto> history = performance.getHistory();

    if (history.size() != 0) {
      BigDecimal firstDayValue = history.get(0).getPrice();
      BigDecimal change = performanceValue.subtract(firstDayValue);
      performance.setChange(change);
      BigDecimal changesPercentage =
          change.divide(firstDayValue, MathContext.DECIMAL128).multiply(BigDecimal.valueOf(100));
      performance.setChangesPercentage(changesPercentage);
    }

    performance.setValue(performanceValue);
    return performance;
  }

  private List<PerformanceHistoryItemDto> updatePerformanceHistory(
      List<PerformanceHistoryItemDto> history, BigDecimal performanceValue) {
    if (history.size() == DAYS_OF_HISTORY) { // store only only a certain days amount of history
      history.remove(0);
    }
    PerformanceHistoryItemDto historyItem = new PerformanceHistoryItemDto();
    historyItem.setDate(ZonedDateTime.now().format(dateFormatter));
    historyItem.setPrice(performanceValue);
    history.add(historyItem);
    return history;
  }

  private List<GeneralInfoInSearchDto> getAssets(Portfolio portfolio) {
    List<String> tickers =
        portfolio.getAssets().stream().map(Asset::getTicker).collect(Collectors.toList());
    if (tickers.isEmpty()) {
      return new ArrayList<>();
    }
    String allTickersString = StringUtils.join(tickers, ",");
    log.debug(
        "Asset tickers for user with id {}: {}", portfolio.getUser().getId(), allTickersString);
    return financialModellingClient.getCompanyInfos(allTickersString);
  }

  private BigDecimal calculatePerformanceValue(
      Portfolio portfolio, Map<String, BigDecimal> tickerPriceMap) {
    List<BigDecimal> assetTotalPrices = new ArrayList<>();
    tickerPriceMap.forEach(
        (ticker, price) -> {
          Asset byPortfolioIdAndTicker =
              assetRepository.findByPortfolioIdAndTicker(portfolio.getId(), ticker);
          BigDecimal assetTotalPrice = price.multiply(byPortfolioIdAndTicker.getAmount());
          assetTotalPrices.add(assetTotalPrice);
        });
    return assetTotalPrices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
