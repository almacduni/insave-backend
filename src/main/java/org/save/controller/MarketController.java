package org.save.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.save.indicators.BollingerBands;
import org.save.indicators.MovingAverageConvergenceDivergence;
import org.save.indicators.TradersDynamicIndex;
import org.save.model.dto.indicators.MACDIndicatorResponse;
import org.save.model.dto.market.CurrentCandleDto;
import org.save.model.dto.market.MarketApiResponse;
import org.save.model.dto.market.StockCandleDto;
import org.save.model.entity.common.Stock;
import org.save.model.enums.TimeFrame;
import org.save.service.implementation.IndicatorService;
import org.save.service.serach.MarketService;
import org.save.util.mapper.indicators.BBIndicatorMapper;
import org.save.util.mapper.indicators.MACDIndicatorMapper;
import org.save.util.mapper.indicators.TDIIndicatorMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/market")
@RequiredArgsConstructor
public class MarketController {

  private final MarketService marketService;
  private final IndicatorService indicatorService;
  private final MACDIndicatorMapper macdIndicatorMapper;
  private final BBIndicatorMapper bbIndicatorMapper;
  private final TDIIndicatorMapper tdiIndicatorMapper;

  @GetMapping("/stock-info")
  public ResponseEntity<Stock> getMarketStockInfo(String ticker) {

    Stock stock = marketService.getMarketStockInfo(ticker);
    if (stock == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(stock, HttpStatus.OK);
  }

  @GetMapping("/stock-candles-eod")
  public MarketApiResponse<List<StockCandleDto>> getMarketStockCandlesEndOfDay(
      String ticker, String date) {
    List<StockCandleDto> stockCandles = marketService.getMarketStockCandleEndOfDay(ticker, date);

    if (stockCandles == null) {
      return new MarketApiResponse<>(null, null, "STOCK PROBLEM", 1);
    }
    CurrentCandleDto currentCandle = marketService.getCurrent(ticker);

    return new MarketApiResponse<>(stockCandles, currentCandle, "All RIGHT", 0);
  }

  @GetMapping("/stock-candles-intraday")
  public MarketApiResponse<List<StockCandleDto>> getMarketStockCandles(String ticker) {

    List<StockCandleDto> stockCandles = marketService.getMarketStockCandlesIntraday(ticker);
    CurrentCandleDto currentCandle = marketService.getCurrent(ticker);

    if (stockCandles == null && currentCandle == null) {
      return new MarketApiResponse<>(null, null, "STOCK PROBLEM", 12);
    }

    return new MarketApiResponse<>(stockCandles, currentCandle, "ALL RIGHT", 0);
  }

  @GetMapping("/stock-candles-intraday-interval")
  public MarketApiResponse<List<StockCandleDto>> getMarketStockCandlesIntradayInterval(
      String ticker, String interval) {

    List<StockCandleDto> stockCandles =
        marketService.getMarketStockCandlesIntradayInterval(ticker, interval);
    CurrentCandleDto currentCandle = marketService.getCurrent(ticker);

    if (stockCandles == null) return new MarketApiResponse<>(null, null, "STOCK PROBLEM", 1);

    return new MarketApiResponse<>(stockCandles, currentCandle, "ALL RIGHT", 0);
  }

  @GetMapping("/stock-candles-interval")
  public MarketApiResponse<List<StockCandleDto>> getMarketStockCandlesInterval(
      String ticker, String from, String to) {

    List<StockCandleDto> stockCandles =
        marketService.getMarketStockCandleInterval(ticker, from, to);
    CurrentCandleDto currentCandle = marketService.getCurrent(ticker);

    if (stockCandles == null) return new MarketApiResponse<>(null, null, "STOCK PROBLEM", 1);

    return new MarketApiResponse<>(stockCandles, currentCandle, "ALL RIGHT", 0);
  }

  @GetMapping("/stock-candles-intraday-daily")
  public MarketApiResponse<List<StockCandleDto>> getMarketStockCandles(
      String ticker, TimeFrame timeFrame) {

    List<StockCandleDto> stockCandles = marketService.getMarketStockCandles(ticker, timeFrame);
    CurrentCandleDto currentCandle = marketService.getCurrent(ticker);

    if (stockCandles == null && currentCandle == null)
      return new MarketApiResponse<>(null, null, "STOCK PROBLEM NOT FOUND", 12);

    return new MarketApiResponse<>(stockCandles, currentCandle, "ALL RIGHT", 0);
  }

  @GetMapping("/crypto-stock-candles-intraday-daily")
  public MarketApiResponse<List<StockCandleDto>> getMarketCryptoStockCandlesIntradayDaily(
      String ticker, int timeseries) {

    List<StockCandleDto> stockCandles =
        marketService.getMarketCryptoStockCandlesIntradayDaily(ticker, timeseries);
    //        CurrentCandle currentCandle = marketService.getCurrent(ticker);

    if (stockCandles == null)
      return new MarketApiResponse<>(null, null, "STOCK PROBLEM NOT FOUND", 12);

    return new MarketApiResponse<>(stockCandles, null, "ALL RIGHT", 0);
  }

  @GetMapping("indicators/guppyIntraday")
  public ResponseEntity<Map<Integer, List<Double>>> getGuppy(
      String ticker, @RequestParam(required = false) TimeFrame timeFrame) {

    List<StockCandleDto> stockCandles =
        marketService.getMarketStockCandles(
            ticker, Objects.requireNonNullElse(timeFrame, TimeFrame.ONE_DAY));
    Map<Integer, List<Double>> emaIndicators =
        indicatorService.createManyEmaIndicators(stockCandles);

    return new ResponseEntity<>(emaIndicators, HttpStatus.OK);
  }

  @GetMapping("indicators/rsi")
  public ResponseEntity<?> getRsi(
      @RequestParam String ticker,
      @RequestParam TimeFrame timeFrame,
      @RequestParam(defaultValue = "14") Integer period) {
    return new ResponseEntity<>(indicatorService.getRsi(ticker, timeFrame, period), HttpStatus.OK);
  }

  @GetMapping("indicators/adx")
  public ResponseEntity<?> getAdx(
      @RequestParam String ticker,
      @RequestParam TimeFrame timeFrame,
      @RequestParam(defaultValue = "14") Integer period) {
    return new ResponseEntity<>(indicatorService.getAdx(ticker, timeFrame, period), HttpStatus.OK);
  }

  @GetMapping("indicators/sma")
  public ResponseEntity<?> getSma(
      @RequestParam String ticker,
      @RequestParam TimeFrame timeFrame,
      @RequestParam(defaultValue = "14") Integer period) {
    return new ResponseEntity<>(indicatorService.getSma(ticker, timeFrame, period), HttpStatus.OK);
  }

  @GetMapping("indicators/ema")
  public ResponseEntity<?> getEma(
      @RequestParam String ticker,
      @RequestParam TimeFrame timeFrame,
      @RequestParam(defaultValue = "14") Integer period) {
    return new ResponseEntity<>(indicatorService.getEma(ticker, timeFrame, period), HttpStatus.OK);
  }

  @GetMapping("indicators/macd")
  public ResponseEntity<MACDIndicatorResponse> getMacd(
      @RequestParam String ticker,
      @RequestParam TimeFrame timeFrame,
      @RequestParam(defaultValue = "26") Integer slowPeriod,
      @RequestParam(defaultValue = "12") Integer fastPeriod,
      @RequestParam(defaultValue = "9") Integer signalPeriod) {
    MovingAverageConvergenceDivergence index =
        indicatorService.getMacd(ticker, timeFrame, fastPeriod, slowPeriod, signalPeriod);
    return new ResponseEntity<>(macdIndicatorMapper.convertToMACDIndResponse(index), HttpStatus.OK);
  }

  @GetMapping("indicators/bb")
  public ResponseEntity<?> getBb(
      @RequestParam String ticker,
      @RequestParam TimeFrame timeFrame,
      @RequestParam(defaultValue = "20") Integer period,
      @RequestParam(defaultValue = "2") Double indexD) {
    BollingerBands indicator = indicatorService.getBb(ticker, timeFrame, period, indexD);
    return new ResponseEntity<>(bbIndicatorMapper.convertToBBResponse(indicator), HttpStatus.OK);
  }

  @GetMapping("indicators/tema")
  public ResponseEntity<?> getTema(
      @RequestParam String ticker,
      @RequestParam TimeFrame timeFrame,
      @RequestParam(defaultValue = "8") Integer ema1Period,
      @RequestParam(defaultValue = "20") Integer ema2Period,
      @RequestParam(defaultValue = "50") Integer ema3Period) {
    return new ResponseEntity<>(
        indicatorService.getTema(ticker, timeFrame, ema1Period, ema2Period, ema3Period),
        HttpStatus.OK);
  }

  @GetMapping("indicators/tdi")
  public ResponseEntity<?> getTdi(
      @RequestParam String ticker,
      @RequestParam TimeFrame timeFrame,
      @RequestParam(defaultValue = "13") Integer rsiPeriod,
      @RequestParam(defaultValue = "2") Integer fastSmPeriod,
      @RequestParam(defaultValue = "7") Integer slowSmPeriod,
      @RequestParam(defaultValue = "34") Integer bbPeriod) {
    TradersDynamicIndex index =
        indicatorService.getTdi(ticker, timeFrame, rsiPeriod, fastSmPeriod, slowSmPeriod, bbPeriod);
    return new ResponseEntity<>(tdiIndicatorMapper.convertToTDIResponse(index), HttpStatus.OK);
  }
}
