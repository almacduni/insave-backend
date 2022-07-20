package org.save.client;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.save.model.dto.financialmodelling.CompanyDescription;
import org.save.model.dto.financialmodelling.FmpDto;
import org.save.model.dto.financialmodelling.FmpEarningsCalendar;
import org.save.model.dto.search.GeneralInfoInSearchDto;
import org.save.model.dto.watchlist.WatchlistItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/** Service performs requests to financialmodelingprep api */
@Service
@Log4j2
@RequiredArgsConstructor
public class FinancialModellingClient {

  private static final String HISTORICAL_EARNING_CALENDAR_SUB_URL = "historical/earning_calendar/";
  private static final String KEY_METRICS_TTM_SUB_URL = "key-metrics-ttm/";
  private static final String PROFILE_SUB_URL = "profile/";
  private static final String MARKET_CAPITALIZATION_SUB_URL = "market-capitalization/";
  private static final String QUOTE_SUB_URL = "quote/";
  private static final String RATIOS_TTM_SUB_URL = "ratios-ttm/";

  private static final String LIMIT_PARAM = "&limit=";
  private static final String APIKEY_PARAM = "?apikey=";

  @Value("${insave.integration.finances.financial_modelling.api_key}")
  private String apiKey;

  @Value("${insave.integration.finances.financial_modelling.url}")
  private final String financialModellingUrl;

  private final RestTemplate restTemplate;

  /**
   * Gets company additional info for provided ticker
   *
   * @param ticker ticker to get company additional info
   * @return found company additional info or throw an exception if no info was found
   */
  public WatchlistItem getCompanyAdditionalInfo(String ticker) {
    var url = buildUrl(ticker, PROFILE_SUB_URL);
    return getFirstElementOrThrowAnException(url, WatchlistItem.class);
  }

  /**
   * Gets company info by provided ticker
   *
   * @param ticker ticker to get company
   * @return found company info or throw an exception if no info was found
   */
  public GeneralInfoInSearchDto getCompanyInfo(String ticker) {
    var url = buildUrl(ticker, QUOTE_SUB_URL);
    return getFirstElementOrThrowAnException(url, GeneralInfoInSearchDto.class);
  }

  /**
   * Gets company info by provided ticker
   *
   * @param ticker ticker to get company
   * @return found company info or throw an exception if no info was found
   */
  public List<GeneralInfoInSearchDto> getCompanyInfos(String ticker) {
    var url = buildUrl(ticker, QUOTE_SUB_URL);
    return getListResponse(url, GeneralInfoInSearchDto.class);
  }

  /**
   * Gets earning calendars for ticker
   *
   * @param ticker ticker
   * @return found calendars
   */
  public List<FmpEarningsCalendar> getEarningCalendars(String ticker) {
    String url = buildUrl(ticker, HISTORICAL_EARNING_CALENDAR_SUB_URL, 6);
    return getListResponse(url, FmpEarningsCalendar.class);
  }

  /**
   * Gets list {@link FmpDto} by provided ticker
   *
   * @param ticker ticker
   * @return found list
   */
  public List<FmpDto> getFmpRatios(String ticker) {
    String url = buildUrl(ticker, KEY_METRICS_TTM_SUB_URL, 10);
    return getListResponse(url, FmpDto.class);
  }

  /**
   * Gets watch list item for ticker
   *
   * @param ticker ticker to get watch list item
   * @return watch list item or throw an exception if no info was found
   */
  public WatchlistItem getWatchListItem(String ticker) {
    var url = buildUrl(ticker, QUOTE_SUB_URL);
    return getFirstElementOrThrowAnException(url, WatchlistItem.class);
  }

  /**
   * Gets optional market capitalization for ticker
   *
   * @param ticker ticker
   * @return optional market capitalization
   */
  public Optional<CompanyDescription> getMarketCapitalization(String ticker) {
    return getDescription(ticker, MARKET_CAPITALIZATION_SUB_URL);
  }

  /**
   * Gets item statistic for ticker
   *
   * @param ticker ticker
   * @return found item statistic
   */
  public Optional<CompanyDescription> getItemStatistic(String ticker) {
    return getDescription(ticker, RATIOS_TTM_SUB_URL);
  }

  /**
   * Gets company description by ticker
   *
   * @param ticker ticker
   * @return found company description
   */
  public Optional<CompanyDescription> getCompanyDescription(String ticker) {
    return getDescription(ticker, PROFILE_SUB_URL);
  }

  private Optional<CompanyDescription> getDescription(String ticker, String profileSubUrl) {
    var url = buildUrl(ticker, profileSubUrl);
    try {
      CompanyDescription[] foundCompanyDescriptions =
          restTemplate.getForEntity(url, CompanyDescription[].class).getBody();
      return Arrays.stream(Objects.requireNonNull(foundCompanyDescriptions)).findFirst();
    } catch (Exception e) {
      log.error("Couldn't load data for ticker {}, exception - {}", ticker, e);
      return Optional.empty();
    }
  }

  private <T> T getFirstElementOrThrowAnException(String url, Class<T> clazz) {
    List<T> foundList = getListResponse(url, clazz);
    if (isEmpty(foundList)) {
      throw new IllegalArgumentException("Couldn't load list of data from URL - " + url);
    } else {
      return foundList.get(0);
    }
  }

  private <T> List<T> getListResponse(String url, Class<T> clazz) {
    ParameterizedTypeReference<List<T>> parameterizedTypeReference =
        ParameterizedTypeReference.forType(TypeUtils.parameterize(List.class, clazz));
    return restTemplate.exchange(url, HttpMethod.GET, null, parameterizedTypeReference).getBody();
  }

  private String buildUrl(String ticker, String subUrl) {
    return buildUrl(ticker, subUrl, null);
  }

  private String buildUrl(String ticker, String subUrl, Integer limit) {
    StringBuilder sb =
        new StringBuilder(
            financialModellingUrl + subUrl + ticker.toUpperCase() + APIKEY_PARAM + apiKey);
    if (limit != null) {
      sb.append(LIMIT_PARAM).append(limit);
    }
    return sb.toString();
  }
}
