package org.save.util;

import static org.save.model.dto.polygon.TickerAggregatesResponse.Aggregate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.save.model.dto.polygon.CloseStockDto;
import org.save.model.dto.polygon.PrevClose;
import org.save.model.dto.polygon.TickerAggregatesResponse;
import org.save.model.enums.TimeFrame;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolygonAPI {

  @Value("${insave.integration.finances.polygon.api_key}")
  private String API_KEY;

  private static final int NUMBER_OF_RETURNED_VALUES = 128;
  private static final long SECOND_IN_MILLIS = 1000;
  private static final long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
  private static final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
  private static final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;
  private static final long MONTH_IN_MILLIS = DAY_IN_MILLIS * 28;
  private static final long STOCK_MARKET_OPEN_TIME_IN_MILLIS =
      13 * HOUR_IN_MILLIS + 30 * MINUTE_IN_MILLIS;

  private final RestTemplate restTemplate;

  private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final String URL_POLIGON_API_STOCK_WITH_ONE_D_TF =
      "https://api.polygon.io/v2/aggs/ticker/{ticker}/range/1/day/"
          + "{firstMonth}/{lastMonth}?unadjusted=false&sort=desc&limit=50000&apiKey={API_KEY}";

  public BigDecimal getTickerAMG(String ticker) {
    BigDecimal tickerAMG = new BigDecimal("0.0");

    try {
      TickerAggregatesResponse tickerAggregatesResponse =
          getTickerAggregates(ticker, TimeFrame.ONE_MONTH);
      if (tickerAggregatesResponse.getResults() != null) {
        int aggregatesSize = tickerAggregatesResponse.getResults().size();

        if (aggregatesSize > 1) {
          for (Aggregate aggregate : tickerAggregatesResponse.getResults()) {

            tickerAMG =
                tickerAMG.add(
                    aggregate
                        .getClose()
                        .subtract(aggregate.getOpen())
                        .divide(aggregate.getOpen(), RoundingMode.DOWN));
          }

          tickerAMG = tickerAMG.multiply(BigDecimal.valueOf(100L));
          tickerAMG = tickerAMG.divide(BigDecimal.valueOf(aggregatesSize), RoundingMode.DOWN);
        }

        log.info(ticker + " AMG:{} aggregatesSize:{}", tickerAMG, aggregatesSize);
      }

    } catch (RestClientException exception) {
      log.error("POLYGON API request exception:", exception);
    }

    return tickerAMG;
  }

  public TickerAggregatesResponse getTickerAggregates(String ticker, TimeFrame timeFrame) {
    final String firstMonth =
        LocalDate.now(Clock.systemUTC()).minus(2, ChronoUnit.YEARS).format(dateTimeFormatter);

    final String lastMonth = LocalDate.now(Clock.systemUTC()).format(dateTimeFormatter);

    final String url =
        "https://api.polygon.io/v2/aggs/ticker/{ticker}/range/"
            + timeFrame.getTimeFrame()
            + "/{firstMonth}/{lastMonth}?unadjusted=false&sort=desc&limit=50000&apiKey={API_KEY}";
    return restTemplate
        .exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<TickerAggregatesResponse>() {},
            ticker,
            firstMonth,
            lastMonth,
            API_KEY)
        .getBody();
  }

  public TickerAggregatesResponse getTickerAggregatesHours(String ticker, TimeFrame timeFrame)
      throws RestClientException {

    final long helpFrom = System.currentTimeMillis() - 50 * 12 * MONTH_IN_MILLIS;
    final long from = helpFrom - helpFrom % DAY_IN_MILLIS + STOCK_MARKET_OPEN_TIME_IN_MILLIS;
    final long to = System.currentTimeMillis();

    final String url =
        "https://api.polygon.io/v2/aggs/ticker/{ticker}/range/"
            + timeFrame.getTimeFrame()
            + "/{from}/{to}?unadjusted=false&sort=desc&limit=50000&apiKey={API_KEY}";

    return restTemplate
        .exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<TickerAggregatesResponse>() {},
            ticker,
            from,
            to,
            API_KEY)
        .getBody();
  }

  public List<CloseStockDto> getMarketStockHistoricalDaily(String ticker) {
    DateTimeFormatter dateTimeFormatter1 =
        DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);
    String firstMoth =
        LocalDate.now(Clock.systemUTC()).minus(180, ChronoUnit.DAYS).format(dateTimeFormatter);

    String lastMonth = LocalDate.now(Clock.systemUTC()).format(dateTimeFormatter);

    List<Aggregate> tickerAggregatesResponseList =
        restTemplate
            .exchange(
                URL_POLIGON_API_STOCK_WITH_ONE_D_TF,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<TickerAggregatesResponse>() {},
                ticker,
                firstMoth,
                lastMonth,
                API_KEY)
            .getBody()
            .getResults();

    return tickerAggregatesResponseList.stream()
        .map(
            oldFormat -> {
              CloseStockDto newFormat =
                  new CloseStockDto(
                      dateTimeFormatter1.format(Instant.ofEpochMilli(oldFormat.getDate())),
                      oldFormat.getClose());
              return newFormat;
            })
        .limit(NUMBER_OF_RETURNED_VALUES)
        .collect(Collectors.toList());
  }

  public PrevClose getPrevClose(String ticker) {
    String url =
        "https://api.polygon.io/v2/aggs/ticker/{ticker}/prev?adjusted=true&apiKey={API_KEY}";
    return restTemplate
        .exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<PrevClose>() {},
            ticker,
            API_KEY)
        .getBody();
  }
}
