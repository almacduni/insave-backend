package org.save.service.serach;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.save.client.FinancialModellingClient;
import org.save.exception.InvalidArgumentException;
import org.save.exception.InvalidTickerException;
import org.save.exception.NoSuchObjectException;
import org.save.model.dto.financialmodelling.CompanyDescription;
import org.save.model.dto.financialmodelling.EarningSurpriseDto;
import org.save.model.dto.financialmodelling.FinancialStatementDto;
import org.save.model.dto.financialmodelling.FmpEarningsCalendar;
import org.save.model.dto.search.CompanyInfoResponse;
import org.save.model.dto.search.GeneralInfoInSearchDto;
import org.save.model.dto.search.OverallCompanyDescriptionResponse;
import org.save.model.dto.search.SearchTickerResponse;
import org.save.model.entity.watchlist.WatchlistItemDescription;
import org.save.repo.TickerRepository;
import org.save.repo.WatchlistItemDescriptionRepository;
import org.save.repo.playlist.PlaylistRepository;
import org.save.service.implementation.EarningSurpriseService;
import org.save.service.implementation.FinancialStatementService;
import org.save.util.PolygonAPI;
import org.save.util.mapper.CategoriesResponseMapper;
import org.save.util.mapper.TickerMapper;
import org.save.util.parsers.FinvizParser;
import org.save.util.parsers.WikipediaParser;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class CompanyDescriptionService {

  private static final int SIZE_OF_THREAD_POOL = 2;
  private static final String SELECT_FIRST_SENTENCE_REGEX = "^.+?\\.\\s[A-ZА-Я]";

  private final FinancialStatementService statementService;
  private final EarningSurpriseService earningSurpriseService;
  private final TickerMapper tickerMapper;
  private final PlaylistRepository playlistRepository;
  private final TickerRepository tickerRepository;
  private final WikipediaParser wikipediaParser;
  private final PolygonAPI polygonAPI;
  private final WatchlistItemDescriptionRepository watchlistItemDescriptionRepository;
  private final ExecutorService executorService = Executors.newFixedThreadPool(SIZE_OF_THREAD_POOL);
  private final CategoriesResponseMapper playlistMapper;
  private final FinvizParser finvizParser;
  private final FinancialModellingClient financialModellingClient;

  public PageableSearchResponse search(String tickerName, Integer page, Integer pageLimit) {
    verifySearchInputData(page, pageLimit);
    var pageRequest = PageRequest.of(page - 1, pageLimit);
    var tickerPage =
        tickerRepository.findAllByNameIgnoreCaseStartingWithOrCompanyIgnoreCaseStartingWith(
            tickerName, tickerName, pageRequest);

    List<SearchTickerResponse> tickersResponse =
        tickerPage.getContent().stream()
            .map(tickerMapper::convertToSearchTickerResponse)
            .collect(Collectors.toList());

    return PageableSearchResponse.builder()
        .searchTickerResponseList(tickersResponse)
        .currentPage(tickerPage.getNumber() + 1)
        .totalCount(tickerPage.getTotalElements())
        .offset(tickerPage.getSize())
        .build();
  }

  @SneakyThrows
  public OverallCompanyDescriptionResponse getOverallCompanyDescription(String tickerName) {
    var ticker =
        tickerRepository
            .findTickerByName(tickerName)
            .orElseThrow(
                () ->
                    new NoSuchObjectException(
                        "there is no ticker with provided name" + tickerName));
    Future<List<EarningSurpriseDto>> earningSurpriseList =
        executorService.submit(() -> earningSurpriseService.getEarningSurprise(tickerName));
    Future<List<FinancialStatementDto>> financialStatementList =
        executorService.submit(() -> statementService.getStatFinancialStatement(tickerName));
    var companyDescriptionByTicker =
        getCompanyDescription(tickerName.toUpperCase()).orElse(new CompanyDescription());
    var playlistList = playlistRepository.findByTickers(ticker);
    var playlistResponseList =
        playlistList.stream()
            .map(playlistMapper::convertToPlayListResponse)
            .collect(Collectors.toList());

    return new OverallCompanyDescriptionResponse(
        companyDescriptionByTicker,
        financialStatementList.get(),
        earningSurpriseList.get(),
        playlistResponseList);
  }

  public CompanyInfoResponse getCompanyInfo(String ticker) {
    ticker = ticker.toUpperCase();
    if (!tickerRepository.existsByName(ticker)) {
      throw new InvalidTickerException("Invalid ticker");
    }

    var companyInfo = financialModellingClient.getCompanyInfo(ticker);
    setDescriptionAndLogo(ticker, companyInfo);
    companyInfo.setHistorical(polygonAPI.getMarketStockHistoricalDaily(ticker));
    saveWatchlistItem(companyInfo);
    return new CompanyInfoResponse(companyInfo);
  }

  private void verifySearchInputData(Integer page, Integer pageLimit) {
    if (page <= 0 || pageLimit <= 0) {
      throw new InvalidArgumentException(
          "Page starts from one. Provided: "
              + page
              + " Page limit minimum value is one. Provided: "
              + pageLimit);
    }
  }

  private Optional<CompanyDescription> getCompanyDescription(String ticker) {
    try {
      var getCompanyDescriptionFromFinViz = finvizParser.getCompanyDescription(ticker);
      CompanyDescription companyDescription = getDescription(ticker);
      setMarketCapitalization(ticker, companyDescription);
      setItemStastistic(ticker, companyDescription);
      setEarnings(ticker, companyDescription);

      companyDescription.setRecom(getCompanyDescriptionFromFinViz.getRecom());
      companyDescription.setTargetPrice(getCompanyDescriptionFromFinViz.getTargetPrice());
      companyDescription.setInstOwn(getCompanyDescriptionFromFinViz.getInstOwn());
      companyDescription.setInsiderOwn(getCompanyDescriptionFromFinViz.getInsiderOwn());
      companyDescription.setAmg(polygonAPI.getTickerAMG(ticker));
      companyDescription.setCurrentRatio(
          financialModellingClient.getFmpRatios(ticker).get(0).getCurrentRatio());
      companyDescription.setLeverageRatio(
          financialModellingClient.getFmpRatios(ticker).get(0).getLeverageRatio());

      return Optional.of(companyDescription);
    } catch (Exception exception) {
      log.error(
          "Couldn't get company description for ticker - {}, exception - {}", ticker, exception);
      return Optional.empty();
    }
  }

  private void setEarnings(String ticker, CompanyDescription companyDescription) {
    List<FmpEarningsCalendar> fmpEarningsCalendars =
        financialModellingClient.getEarningCalendars(ticker);

    companyDescription.setNextEarningsDate(fmpEarningsCalendars.get(0).getDate());

    if (fmpEarningsCalendars.get(0).getEpsEstimated() == null) {
      fmpEarningsCalendars.remove(0);
    } else {
      fmpEarningsCalendars.remove(fmpEarningsCalendars.size() - 1);
    }

    companyDescription.setEps(fmpEarningsCalendars);
  }

  private void setMarketCapitalization(String ticker, CompanyDescription companyDescription) {
    Optional<CompanyDescription> marketCap =
        financialModellingClient.getMarketCapitalization(ticker);
    companyDescription.setMktCap(
        marketCap.isEmpty()
            ? new BigDecimal("0.0")
            : marketCap.get().getMktCap().setScale(2, RoundingMode.DOWN));
  }

  private void setItemStastistic(String ticker, CompanyDescription companyDescription) {
    Optional<CompanyDescription> optionalItemStatistic =
        financialModellingClient.getItemStatistic(ticker);
    if (optionalItemStatistic.isPresent()) {
      CompanyDescription itemStatistic = optionalItemStatistic.get();
      companyDescription.setReturnOnEquityTTM(calculateValue(itemStatistic.getReturnOnEquityTTM()));
      companyDescription.setReturnOnAssetsTTM(calculateValue(itemStatistic.getReturnOnAssetsTTM()));
      companyDescription.setDividendYielPercentageTTM(
          calculateValue(itemStatistic.getDividendYielPercentageTTM()));
      companyDescription.setPeRationTTM(calculateValue(itemStatistic.getPeRationTTM()));
    } else {
      log.error(
          "Couldn't find item statistic for company description, will be skipped. Ticker - {}",
          ticker);
    }
  }

  private CompanyDescription getDescription(String ticker) {
    Optional<CompanyDescription> optionalCompanyDescription =
        financialModellingClient.getCompanyDescription(ticker);
    if (optionalCompanyDescription.isEmpty()) {
      throw new IllegalArgumentException("Company description is empty for ticker - " + ticker);
    }
    return optionalCompanyDescription.get();
  }

  private double calculateValue(Double value) {
    return value == null ? 0.0 : Math.round(value * 100.0) / 100.0;
  }

  private void setDescriptionAndLogo(String ticker, GeneralInfoInSearchDto companyInfo) {
    Optional<WatchlistItemDescription> watchlistItem =
        watchlistItemDescriptionRepository.findByTicker(ticker);
    if (watchlistItem.isPresent()) {
      var item = watchlistItem.get();
      companyInfo.setDescription(item.getDescription());
      companyInfo.setLogo(item.getLogo());
      return;
    }

    String wikipediaDescription = getWikipediaDescription(ticker);
    companyInfo.setDescription(wikipediaDescription);

    var additionalInfo = financialModellingClient.getCompanyAdditionalInfo(ticker);
    companyInfo.setLogo(additionalInfo.getLogo());
    if (companyInfo.getDescription().isEmpty()) {
      companyInfo.setDescription(additionalInfo.getDescription());
    }
  }

  private String getWikipediaDescription(String ticker) {
    Pattern firstSentencePattern = Pattern.compile(SELECT_FIRST_SENTENCE_REGEX);
    var wikipediaDescription = wikipediaParser.getDescriptionData(ticker).trim();
    Matcher firstSentence = firstSentencePattern.matcher(wikipediaDescription);
    while (firstSentence.find()) {
      wikipediaDescription =
          wikipediaDescription.substring(firstSentence.start(), firstSentence.end() - 2);
    }
    return wikipediaDescription;
  }

  private void saveWatchlistItem(GeneralInfoInSearchDto companyInfo) {
    String ticker = companyInfo.getTicker();
    if (watchlistItemDescriptionRepository.findByTicker(ticker).isPresent()) {
      return;
    }
    var watchlistItem =
        new WatchlistItemDescription(ticker, companyInfo.getDescription(), companyInfo.getLogo());
    watchlistItemDescriptionRepository.save(watchlistItem);
  }
}
