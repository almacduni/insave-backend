package org.save.service.serach;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.save.model.dto.market.CurrentCandleDto;
import org.save.model.dto.market.StockCandleDto;
import org.save.model.dto.polygon.TickerAggregatesResponse;
import org.save.model.entity.common.Stock;
import org.save.model.enums.Currency;
import org.save.model.enums.TimeFrame;
import org.save.util.DateParseUtils;
import org.save.util.PolygonAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
@Log4j2
@RequiredArgsConstructor
public class MarketService {

  private static final long SECOND_IN_MILLIS = 1000;
  private static final long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
  private static final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
  private static final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;

  // OPEN and CLOSE hours in GMT
  private static final long STOCK_MARKET_OPEN_TIME_IN_MILLIS =
      13 * HOUR_IN_MILLIS + 29 * MINUTE_IN_MILLIS;
  private static final long STOCK_MARKET_CLOSE_TIME_IN_MILLIS =
      19 * HOUR_IN_MILLIS + 59 * MINUTE_IN_MILLIS;

  @Value("${insave.integration.finances.financial_modelling.api_key}")
  private String FINANCIAL_MODEL_KEY;

  private final PolygonAPI polygonAPI;

  public Stock getMarketStockInfo(String ticker) {
    String URL_ADDRESS =
        "https://api.polygon.io/v1/meta/symbols/"
            + ticker
            + "/company?apiKey=JOD_Y4F3PP0kiIR7OcufsRl_lXUAQ8jP";

    Stock stock = new Stock();

    URL url;
    URLConnection urlConnection;
    BufferedReader bufferedReader;
    StringBuilder content = new StringBuilder();
    String tempString;

    try {
      url = new URL(URL_ADDRESS);

      urlConnection = url.openConnection();
      bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

      while ((tempString = bufferedReader.readLine()) != null)
        content.append(tempString).append("\n");

    } catch (IOException e) {
      e.printStackTrace();
    }

    JSONObject jsonObject = new JSONObject(content.toString());

    stock.setTicker(jsonObject.getString("symbol"));
    stock.setLogo(jsonObject.optString("logo"));
    stock.setCountry(jsonObject.optString("country"));
    stock.setIndustry(jsonObject.optString("industry"));
    stock.setPhone(jsonObject.optString("phone"));
    stock.setUrl(jsonObject.optString("url"));
    stock.setDescription(jsonObject.optString("description"));
    stock.setLogo(jsonObject.optString("logo"));
    stock.setName(jsonObject.optString("name"));
    stock.setCurrency(Currency.USD);

    return stock;
  }

  public CurrentCandleDto getCurrent(String ticker) {
    String URL_ADDRESS =
        "https://financialmodelingprep.com/api/v3/quote/"
            + ticker
            + "?apikey="
            + FINANCIAL_MODEL_KEY;
    RestTemplate restTemplate = new RestTemplate();
    try {
      return Arrays.stream(
              Objects.requireNonNull(
                  restTemplate.getForEntity(URL_ADDRESS, CurrentCandleDto[].class).getBody()))
          .findFirst()
          .orElse(null);
    } catch (HttpStatusCodeException e) {
      log.error(e.getMessage());
      return null;
    }
  }

  public List<StockCandleDto> getMarketStockCandlesIntraday(String ticker) {
    String URL_ADDRESS =
        "https://financialmodelingprep.com/api/v3/historical-chart/30min/"
            + ticker
            + "?apikey="
            + FINANCIAL_MODEL_KEY;

    StockCandleDto stockCandle;
    List<StockCandleDto> stockCandles = new ArrayList<>();

    URL url;
    URLConnection urlConnection;
    BufferedReader bufferedReader;
    StringBuilder content = new StringBuilder();
    String tempString;

    try {
      url = new URL(URL_ADDRESS);

      urlConnection = url.openConnection();
      bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

      while ((tempString = bufferedReader.readLine()) != null)
        content.append(tempString).append("\n");

    } catch (IOException e) {
      e.printStackTrace();
    }

    JSONArray jsonArray = new JSONArray(content.toString());

    for (int i = 0; i < jsonArray.length(); i++) {
      stockCandle = new StockCandleDto();

      stockCandle.setOpen(
          jsonArray.getJSONObject(i).getBigDecimal("open").setScale(2, RoundingMode.DOWN));
      stockCandle.setHigh(
          jsonArray.getJSONObject(i).getBigDecimal("high").setScale(2, RoundingMode.DOWN));
      stockCandle.setLow(
          jsonArray.getJSONObject(i).getBigDecimal("low").setScale(2, RoundingMode.DOWN));
      stockCandle.setClose(
          jsonArray.getJSONObject(i).getBigDecimal("close").setScale(2, RoundingMode.DOWN));
      stockCandle.setVolume(
          jsonArray.getJSONObject(i).getBigDecimal("volume").setScale(2, RoundingMode.DOWN));
      stockCandle.setDate(
          DateParseUtils.simpleFormatterDate(
              jsonArray.getJSONObject(i).get("date").toString(), "yyyy-MM-dd HH:mm"));

      stockCandles.add(stockCandle);
    }
    stockCandles = stockCandles.subList(0, Math.min(stockCandles.size(), 61));
    Collections.reverse(stockCandles);

    StockCandleDto last = stockCandles.get(stockCandles.size() - 1);
    last.setClose(getCurrent(ticker).getPrice());
    stockCandles.set(stockCandles.size() - 1, last);

    return stockCandles;
  }

  public List<StockCandleDto> getMarketCryptoStockCandlesIntradayDaily(
      String ticker, int timeseries) {
    String URL_ADDRESS =
        "https://financialmodelingprep.com/api/v3/historical-price-full/"
            + ticker
            + "/"
            + "?timeseries="
            + timeseries
            + "&apikey="
            + FINANCIAL_MODEL_KEY;

    StockCandleDto stockCandle;
    List<StockCandleDto> stockCandles = new ArrayList<>();

    URL url;
    URLConnection urlConnection;
    BufferedReader bufferedReader;
    StringBuilder content = new StringBuilder();
    String tempString;

    try {
      url = new URL(URL_ADDRESS);

      urlConnection = url.openConnection();
      bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

      while ((tempString = bufferedReader.readLine()) != null)
        content.append(tempString).append("\n");

    } catch (IOException e) {
      e.printStackTrace();
    }

    JSONObject jsonObject = new JSONObject(content.toString());
    JSONArray jsonArray = new JSONArray(jsonObject.getJSONArray("historical"));

    for (int i = 0; i < jsonArray.length(); i++) {
      stockCandle = new StockCandleDto();

      stockCandle.setOpen(jsonArray.getJSONObject(i).getBigDecimal("open"));
      stockCandle.setHigh(jsonArray.getJSONObject(i).getBigDecimal("high"));
      stockCandle.setLow(jsonArray.getJSONObject(i).getBigDecimal("low"));
      stockCandle.setClose(jsonArray.getJSONObject(i).getBigDecimal("close"));
      stockCandle.setVolume(
          jsonArray.getJSONObject(i).getBigDecimal("volume").setScale(2, RoundingMode.DOWN));
      stockCandle.setDate(
          DateParseUtils.simpleFormatterDate(
              jsonArray.getJSONObject(i).get("date").toString(), "yyyy-MM-dd"));

      stockCandles.add(stockCandle);
    }

    Collections.reverse(stockCandles);

    StockCandleDto last = stockCandles.get(stockCandles.size() - 1);
    last.setClose(getCurrent(ticker).getPrice());
    stockCandles.set(stockCandles.size() - 1, last);

    return stockCandles;
  }

  public List<StockCandleDto> getMarketStockCandles(String ticker, TimeFrame timeFrame) {

    List<StockCandleDto> stockCandleList = new ArrayList<>(50000);

    try {
      TickerAggregatesResponse tickerAggregates;
      if (timeFrame == TimeFrame.ONE_HOUR || timeFrame == TimeFrame.FOUR_HOUR)
        tickerAggregates = polygonAPI.getTickerAggregatesHours(ticker, timeFrame);
      else tickerAggregates = polygonAPI.getTickerAggregates(ticker, timeFrame);

      for (TickerAggregatesResponse.Aggregate aggregate : tickerAggregates.getResults()) {
        // Reduce time to one day interval
        Long time = aggregate.getDate() % DAY_IN_MILLIS;

        // Check if time is included in interval between OPEN and CLOSE hours
        if (!(timeFrame == TimeFrame.ONE_MINUTE
                || timeFrame == TimeFrame.FIVE_MINUTES
                || timeFrame == TimeFrame.THIRTY_MINUTES
                || timeFrame == TimeFrame.ONE_HOUR
                || timeFrame == TimeFrame.FOUR_HOUR)
            || time > STOCK_MARKET_OPEN_TIME_IN_MILLIS
                && time <= STOCK_MARKET_CLOSE_TIME_IN_MILLIS) {
          DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

          // Return the date in timezone of stock exchange (GMT-4)
          StockCandleDto stockCandle =
              new StockCandleDto(
                  aggregate.getOpen(),
                  aggregate.getHigh(),
                  aggregate.getLow(),
                  aggregate.getClose(),
                  aggregate.getVolume(),
                  dateTimeFormatter.format(
                      (Instant.ofEpochMilli(aggregate.getDate()).atZone(ZoneId.of("GMT-4")))));

          stockCandleList.add(stockCandle);
        }
      }

    } catch (Exception exception) {
      log.error("getMarketStockCandles exception:", exception);
    }

    // if(stockCandleList.size() > NUMBER_OF_RETURNED_VALUES)
    // stockCandleList.subList(NUMBER_OF_RETURNED_VALUES - 1,stockCandleList.size()).clear();
    Collections.reverse(stockCandleList);
    return stockCandleList;
  }

  @Deprecated
  public List<StockCandleDto> getMarketStockCandlesIntradayDaily(String ticker, int timeseries) {
    String URL_ADDRESS =
        "https://financialmodelingprep.com/api/v3/historical-price-full/"
            + ticker
            + "/"
            + "?timeseries="
            + timeseries
            + "&apikey="
            + FINANCIAL_MODEL_KEY;

    StockCandleDto stockCandle;
    List<StockCandleDto> stockCandles = new ArrayList<>();

    URL url;
    URLConnection urlConnection;
    BufferedReader bufferedReader;
    StringBuilder content = new StringBuilder();
    String tempString;

    try {
      url = new URL(URL_ADDRESS);

      urlConnection = url.openConnection();
      bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

      while ((tempString = bufferedReader.readLine()) != null)
        content.append(tempString).append("\n");

    } catch (IOException e) {
      e.printStackTrace();
    }

    JSONObject jsonObject = new JSONObject(content.toString());
    JSONArray jsonArray = new JSONArray(jsonObject.getJSONArray("historical"));

    for (int i = 0; i < jsonArray.length(); i++) {
      stockCandle = new StockCandleDto();

      stockCandle.setOpen(jsonArray.getJSONObject(i).getBigDecimal("open"));
      stockCandle.setHigh(jsonArray.getJSONObject(i).getBigDecimal("high"));
      stockCandle.setLow(jsonArray.getJSONObject(i).getBigDecimal("low"));
      stockCandle.setClose(jsonArray.getJSONObject(i).getBigDecimal("close"));
      stockCandle.setVolume(
          jsonArray.getJSONObject(i).getBigDecimal("volume").setScale(2, RoundingMode.DOWN));
      stockCandle.setDate(
          DateParseUtils.simpleFormatterDate(
              jsonArray.getJSONObject(i).get("date").toString(), "yyyy-MM-dd"));

      stockCandles.add(stockCandle);
    }

    Collections.reverse(stockCandles);

    StockCandleDto last = stockCandles.get(stockCandles.size() - 1);
    last.setClose(getCurrent(ticker).getPrice());
    stockCandles.set(stockCandles.size() - 1, last);

    return stockCandles;
  }

  public List<StockCandleDto> getMarketStockCandlesIntradayInterval(
      String ticker, String interval) {

    String URL_ADDRESS =
        "https://financialmodelingprep.com/api/v3/historical-chart/"
            + interval
            + "/"
            + ticker
            + "?apikey="
            + FINANCIAL_MODEL_KEY;

    StockCandleDto stockCandle;
    List<StockCandleDto> stockCandles = new ArrayList<>();

    URL url;
    URLConnection urlConnection;
    BufferedReader bufferedReader;
    StringBuilder content = new StringBuilder();
    String tempString;

    try {
      url = new URL(URL_ADDRESS);

      urlConnection = url.openConnection();
      bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

      while ((tempString = bufferedReader.readLine()) != null)
        content.append(tempString).append("\n");

    } catch (IOException e) {
      e.printStackTrace();
    }

    JSONArray jsonArray = new JSONArray(content.toString());

    for (int i = 0; i < jsonArray.length(); i++) {
      stockCandle = new StockCandleDto();

      stockCandle.setOpen(
          jsonArray.getJSONObject(i).getBigDecimal("open").setScale(2, RoundingMode.DOWN));
      stockCandle.setHigh(
          jsonArray.getJSONObject(i).getBigDecimal("high").setScale(2, RoundingMode.DOWN));
      stockCandle.setLow(
          jsonArray.getJSONObject(i).getBigDecimal("low").setScale(2, RoundingMode.DOWN));
      stockCandle.setClose(
          jsonArray.getJSONObject(i).getBigDecimal("close").setScale(2, RoundingMode.DOWN));
      stockCandle.setVolume(
          jsonArray.getJSONObject(i).getBigDecimal("volume").setScale(2, RoundingMode.DOWN));
      stockCandle.setDate(
          DateParseUtils.simpleFormatterDate(
              jsonArray.getJSONObject(i).get("date").toString(), "yyyy-MM-dd HH:mm"));

      stockCandles.add(stockCandle);
    }
    stockCandles = stockCandles.subList(0, Math.min(stockCandles.size(), 61));
    Collections.reverse(stockCandles);

    StockCandleDto last = stockCandles.get(stockCandles.size() - 1);
    last.setClose(getCurrent(ticker).getPrice());
    stockCandles.set(stockCandles.size() - 1, last);

    return stockCandles;
  }

  // YYYY-MM-DD
  public List<StockCandleDto> getMarketStockCandleEndOfDay(String ticker, String date) {

    String URL_ADDRESS =
        "https://financialmodelingprep.com/api/v3/historical-price-full/"
            + ticker
            + "?from="
            + date
            + "&to="
            + date
            + "&apikey="
            + FINANCIAL_MODEL_KEY;

    StockCandleDto stockCandle;
    List<StockCandleDto> stockCandles = new ArrayList<>();

    URL url;
    URLConnection urlConnection;
    BufferedReader bufferedReader;
    StringBuilder content = new StringBuilder();
    String tempString;

    try {
      url = new URL(URL_ADDRESS);

      urlConnection = url.openConnection();
      bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

      while ((tempString = bufferedReader.readLine()) != null)
        content.append(tempString).append("\n");

    } catch (IOException e) {
      e.printStackTrace();
    }

    JSONObject jsonObject = new JSONObject(content.toString());
    JSONArray jsonArray = new JSONArray(jsonObject.getJSONArray("historical"));

    for (int i = 0; i < jsonArray.length(); i++) {
      stockCandle = new StockCandleDto();

      stockCandle.setOpen(
          jsonArray.getJSONObject(i).getBigDecimal("open").setScale(2, RoundingMode.DOWN));
      stockCandle.setHigh(
          jsonArray.getJSONObject(i).getBigDecimal("high").setScale(2, RoundingMode.DOWN));
      stockCandle.setLow(
          jsonArray.getJSONObject(i).getBigDecimal("low").setScale(2, RoundingMode.DOWN));
      stockCandle.setClose(
          jsonArray.getJSONObject(i).getBigDecimal("close").setScale(2, RoundingMode.DOWN));
      stockCandle.setVolume(
          jsonArray.getJSONObject(i).getBigDecimal("volume").setScale(2, RoundingMode.DOWN));
      stockCandle.setDate(
          DateParseUtils.simpleFormatterDate(
              jsonArray.getJSONObject(i).get("date").toString(), "yyyy-MM-dd"));

      stockCandles.add(stockCandle);
    }
    stockCandles = stockCandles.subList(0, Math.min(stockCandles.size(), 61));
    Collections.reverse(stockCandles);

    StockCandleDto last = stockCandles.get(stockCandles.size() - 1);
    last.setClose(getCurrent(ticker).getPrice());
    stockCandles.set(stockCandles.size() - 1, last);

    return stockCandles;
  }

  public List<StockCandleDto> getMarketStockCandleInterval(String ticker, String from, String to) {
    String URL_ADDRESS =
        "https://financialmodelingprep.com/api/v3/historical-price-full/"
            + ticker
            + "?from="
            + from
            + "&to="
            + to
            + "&apikey="
            + FINANCIAL_MODEL_KEY;

    StockCandleDto stockCandle;
    List<StockCandleDto> stockCandles = new ArrayList<>();

    URL url;
    URLConnection urlConnection;
    BufferedReader bufferedReader;
    StringBuilder content = new StringBuilder();
    String tempString;

    try {
      url = new URL(URL_ADDRESS);

      urlConnection = url.openConnection();
      bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

      while ((tempString = bufferedReader.readLine()) != null)
        content.append(tempString).append("\n");

    } catch (IOException e) {
      e.printStackTrace();
    }

    JSONObject jsonObject = new JSONObject(content.toString());
    JSONArray jsonArray = new JSONArray(jsonObject.getJSONArray("historical"));

    for (int i = 0; i < jsonArray.length(); i++) {
      stockCandle = new StockCandleDto();

      stockCandle.setOpen(
          jsonArray.getJSONObject(i).getBigDecimal("open").setScale(2, RoundingMode.DOWN));
      stockCandle.setHigh(
          jsonArray.getJSONObject(i).getBigDecimal("high").setScale(2, RoundingMode.DOWN));
      stockCandle.setLow(
          jsonArray.getJSONObject(i).getBigDecimal("low").setScale(2, RoundingMode.DOWN));
      stockCandle.setClose(
          jsonArray.getJSONObject(i).getBigDecimal("close").setScale(2, RoundingMode.DOWN));
      stockCandle.setVolume(
          jsonArray.getJSONObject(i).getBigDecimal("volume").setScale(2, RoundingMode.DOWN));
      stockCandle.setDate(
          DateParseUtils.simpleFormatterDate(
              jsonArray.getJSONObject(i).get("date").toString(), "yyyy-MM-dd"));

      stockCandles.add(stockCandle);
    }
    stockCandles = stockCandles.subList(0, Math.min(stockCandles.size(), 61));
    Collections.reverse(stockCandles);

    StockCandleDto last = stockCandles.get(stockCandles.size() - 1);
    last.setClose(getCurrent(ticker).getPrice());
    stockCandles.set(stockCandles.size() - 1, last);

    return stockCandles;
  }
}
