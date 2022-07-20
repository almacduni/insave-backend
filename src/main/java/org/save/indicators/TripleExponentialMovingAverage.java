package org.save.indicators;

import java.util.List;
import lombok.Data;

@Data
public class TripleExponentialMovingAverage {

  List<Double> ema1;
  List<Double> ema2;
  List<Double> ema3;

  public TripleExponentialMovingAverage calculate(
      List<Double> prices, Integer ema1Period, Integer ema2Period, Integer ema3Period) {

    ExponentialMovingAverage emaIndicator = new ExponentialMovingAverage();

    ema1 = emaIndicator.calculate(prices, ema1Period).getPeriodEma();
    ema2 = emaIndicator.calculate(prices, ema2Period).getPeriodEma();
    ema3 = emaIndicator.calculate(prices, ema3Period).getPeriodEma();

    return this;
  }
}
