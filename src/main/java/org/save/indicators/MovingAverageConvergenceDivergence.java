package org.save.indicators;

import java.util.List;
import lombok.Data;
import org.save.indicators.utils.ListInitializer;
import org.save.indicators.utils.NumberFormatter;

@Data
public class MovingAverageConvergenceDivergence {
  private static final int CROSSOVER_NONE = 0;
  private static final int CROSSOVER_POSITIVE = 1;
  private static final int CROSSOVER_NEGATIVE = -1;

  private int slowPeriod;
  private int fastPeriod;

  // List of closing prices
  private List<Double> prices;

  // Difference between the long and short EMA (MACD = EMA(LONG_PERIOD) - EMA(SHORT_PERIOD))
  private List<Double> macd;

  /*
      Signal line.
      It is calculated as an exponential moving average
      from the calculated difference of two exponential moving averages.
      SIGNAL = SMA(MACD)
  */
  private List<Double> signal;

  /*
     MACD histogram.
     Difference between the MACD value and the signal line.
     MACDHistogram = MACD – Signal
  */

  public MovingAverageConvergenceDivergence calculate(
      List<Double> prices, int fastPeriod, int slowPeriod, int signalPeriod) {

    this.prices = prices;
    this.slowPeriod = slowPeriod;
    this.fastPeriod = fastPeriod;

    this.macd = ListInitializer.initOfZeroDouble(prices.size());
    this.signal = ListInitializer.initOfZeroDouble(prices.size());

    // Short EMA
    ExponentialMovingAverage emaShort = new ExponentialMovingAverage();
    emaShort.calculate(prices, fastPeriod).getPeriodEma();

    // Long EMA
    ExponentialMovingAverage emaLong = new ExponentialMovingAverage();
    emaLong.calculate(prices, slowPeriod).getPeriodEma();

    calculateMacd(emaShort, emaLong);

    ExponentialMovingAverage signalEma = new ExponentialMovingAverage();

    signal = signalEma.calculate(macd, signalPeriod).getPeriodEma();

    return this;
  }

  // MACD
  private void calculateMacd(ExponentialMovingAverage emaShort, ExponentialMovingAverage emaLong) {
    for (int i = slowPeriod - 1; i < prices.size(); i++) {
      Double macdRes = emaShort.getPeriodEma().get(i) - emaLong.getPeriodEma().get(i);
      macd.set(i, NumberFormatter.round(macdRes, 3));
    }
  }
}
