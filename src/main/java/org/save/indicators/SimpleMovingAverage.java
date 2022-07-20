package org.save.indicators;

import java.util.List;
import lombok.Data;
import org.save.indicators.utils.ListInitializer;
import org.save.indicators.utils.NumberFormatter;

@Data
public class SimpleMovingAverage {

  private List<Double> results;
  private List<Double> prices;
  int period;

  // Arithmetic mean of n closing prices(n = period)
  public SimpleMovingAverage calculate(List<Double> prices, int period) {

    this.prices = prices;
    this.period = period;

    results = ListInitializer.initOfZeroDouble(prices.size());

    calculateSma();

    return this;
  }

  private void calculateSma() {
    int maxLength = prices.size() - period;
    Double smaResult;

    for (int i = 0; i <= maxLength; i++) {
      smaResult =
          prices.subList(i, i + period).stream()
              .mapToDouble(doble -> doble)
              .average()
              .getAsDouble();
      results.set((i + period - 1), NumberFormatter.round(smaResult, 3));
    }
  }
}
