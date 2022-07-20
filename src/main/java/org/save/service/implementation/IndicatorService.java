package org.save.service.implementation;

import com.google.common.base.Predicate;
import eu.verdelhan.ta4j.TADecimal;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.save.indicators.AverageDirectionalMovementIndex;
import org.save.indicators.BollingerBands;
import org.save.indicators.ExponentialMovingAverage;
import org.save.indicators.GuppyMultipleMovingAverageIndicator;
import org.save.indicators.MovingAverageConvergenceDivergence;
import org.save.indicators.RelativeStrengthIndex;
import org.save.indicators.SimpleMovingAverage;
import org.save.indicators.TradersDynamicIndex;
import org.save.indicators.TripleExponentialMovingAverage;
import org.save.model.dto.market.StockCandleDto;
import org.save.model.enums.Group;
import org.save.model.enums.Period;
import org.save.model.enums.TimeFrame;
import org.save.service.serach.MarketService;
import org.save.util.DateParseUtils;
import org.save.util.parsers.EmaParser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IndicatorService {

  private final EmaParser emaParser;
  private final MarketService marketService;

  public Map<Integer, List<Double>> createManyEmaIndicators(List<StockCandleDto> candles) {
    TimeSeries data = new TimeSeries(createTickData(candles));
    Deque<Tick> ticks = new LinkedList<>();
    for (int i = 0; i < data.getEnd(); i++) {
      ticks.addLast(data.getTick(i));
      TimeSeries timeSeries = new TimeSeries(List.copyOf(ticks));
      GuppyMultipleMovingAverageIndicator gmmaIndicator =
          new GuppyMultipleMovingAverageIndicator(new ClosePriceIndicator(timeSeries));
      emaParser.addAveragesToEmaIndicators(gmmaIndicator.getValue(timeSeries.getEnd()));
      if (ticks.size() > 60) {
        ticks.pollFirst();
      }
    }
    return emaParser.getEmaIndicators();
  }

  public static List<Tick> createTickData(List<StockCandleDto> candles) {
    List<Tick> ticks = new ArrayList<>();
    DateTimeFormatter dtf =
        new DateTimeFormatterBuilder()
            .appendPattern(DateParseUtils.PATTERN_FOR_CANDLES)
            .toFormatter();
    for (StockCandleDto candle : candles) {
      DateTime dt = DateTime.parse(candle.getDate(), dtf);
      Tick tick =
          new Tick(
              dt,
              candle.getOpen().doubleValue(),
              candle.getHigh().doubleValue(),
              candle.getLow().doubleValue(),
              candle.getClose().doubleValue(),
              candle.getVolume().doubleValue());
      ticks.add(tick);
    }
    return ticks;
  }

  /**
   * Filters the map, according to the group
   *
   * @param group The group to perform the filtering on
   */
  public static Predicate<Map.Entry<Period, TADecimal>> groupPredicate(final Group group) {
    return entry -> entry.getKey().getGroup().equals(group);
  }

  // Getting a list of closing prices
  private List<Double> getCloseBars(String ticker, TimeFrame timeFrame) {
    List<StockCandleDto> candleList = marketService.getMarketStockCandles(ticker, timeFrame);

    List<Double> close =
        candleList.stream()
            .map(candleListEl -> candleListEl.getClose().doubleValue())
            .collect(Collectors.toList());
    return close;
  }

  public List<Double> getRsi(String ticker, TimeFrame timeFrame, Integer period) {
    List<Double> data = getCloseBars(ticker, timeFrame);

    RelativeStrengthIndex index = new RelativeStrengthIndex();
    return index.calculate(data, period).getRsi();
  }

  public List<Double> getAdx(String ticker, TimeFrame timeFrame, Integer period) {
    List<StockCandleDto> candleList = marketService.getMarketStockCandles(ticker, timeFrame);

    List<Double> high =
        candleList.stream()
            .map(candleListEl -> candleListEl.getHigh().doubleValue())
            .collect(Collectors.toList());

    List<Double> low =
        candleList.stream()
            .map(candleListEl -> candleListEl.getLow().doubleValue())
            .collect(Collectors.toList());

    List<Double> close =
        candleList.stream()
            .map(candleListEl -> candleListEl.getClose().doubleValue())
            .collect(Collectors.toList());

    AverageDirectionalMovementIndex index = new AverageDirectionalMovementIndex();
    return index.calculate(high, low, close, period).getAdx();
  }

  public List<Double> getSma(String ticker, TimeFrame timeFrame, Integer period) {
    List<Double> close = getCloseBars(ticker, timeFrame);

    SimpleMovingAverage index = new SimpleMovingAverage();
    return index.calculate(close, period).getResults();
  }

  public List<Double> getEma(String ticker, TimeFrame timeFrame, Integer period) {
    List<Double> close = getCloseBars(ticker, timeFrame);

    ExponentialMovingAverage index = new ExponentialMovingAverage();
    return index.calculate(close, period).getPeriodEma();
  }

  public MovingAverageConvergenceDivergence getMacd(
      String ticker,
      TimeFrame timeFrame,
      Integer fastPeriod,
      Integer slowPeriod,
      Integer signalPeriod) {
    List<Double> close = getCloseBars(ticker, timeFrame);

    MovingAverageConvergenceDivergence index = new MovingAverageConvergenceDivergence();
    return index.calculate(close, fastPeriod, slowPeriod, signalPeriod);
  }

  public BollingerBands getBb(String ticker, TimeFrame timeFrame, Integer period, Double indexD) {
    List<Double> close = getCloseBars(ticker, timeFrame);

    BollingerBands index = new BollingerBands();
    return index.calculate(close, period, indexD);
  }

  public TripleExponentialMovingAverage getTema(
      String ticker,
      TimeFrame timeFrame,
      Integer ema1Period,
      Integer ema2Period,
      Integer ema3Period) {
    List<Double> close = getCloseBars(ticker, timeFrame);

    TripleExponentialMovingAverage index = new TripleExponentialMovingAverage();
    return index.calculate(close, ema1Period, ema2Period, ema3Period);
  }

  public TradersDynamicIndex getTdi(
      String ticker,
      TimeFrame timeFrame,
      Integer rsiPeriod,
      Integer fastLinePeriod,
      Integer slowLinePeriod,
      Integer bbPeriod) {
    List<Double> close = getCloseBars(ticker, timeFrame);

    TradersDynamicIndex index = new TradersDynamicIndex();
    return index.calculate(close, rsiPeriod, fastLinePeriod, slowLinePeriod, bbPeriod);
  }
}
