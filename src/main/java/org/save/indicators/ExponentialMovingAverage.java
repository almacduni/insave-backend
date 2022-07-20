package org.save.indicators;

import java.util.List;
import lombok.Data;
import org.save.indicators.utils.ListInitializer;
import org.save.indicators.utils.NumberFormatter;

@Data
public class ExponentialMovingAverage {

  // Closing prices
  private List<Double> prices;
  private int period;
  private int indexStart;

  private List<Double> periodSma;

  // Weight factor
  private double smoothingConstant;
  private List<Double> periodEma;

  public ExponentialMovingAverage calculate(List<Double> prices, int period) {

    this.prices = prices;
    this.period = period;

    // Calculation of the weight coefficient
    this.smoothingConstant = 2d / (this.period + 1);

    periodSma = ListInitializer.initOfZeroDouble(prices.size());
    periodEma = ListInitializer.initOfZeroDouble(prices.size());

    indexStart = period - 1;

    calculateEma();

    return this;
  }

  private void calculateEma() {
    for (int i = indexStart; i < prices.size(); i++) {
      SimpleMovingAverage sma = new SimpleMovingAverage();

      List<Double> slice = prices.subList(0, i + 1);
      List<Double> smaResults = sma.calculate(slice, period).getResults();

      periodSma.set(i, smaResults.get(smaResults.size() - 1));

      if (i == indexStart) {
        periodEma.set(i, periodSma.get(i));
      } else if (i > indexStart) {
        // Formula: (Close - EMA(previous day)) x multiplier +
        // EMA(previous day)
        Double periodEmaRes =
            ((prices.get(i) - periodEma.get(i - 1)) * smoothingConstant + periodEma.get(i - 1));
        periodEma.set(i, NumberFormatter.round(periodEmaRes, 2));
      }
    }
  }
}
