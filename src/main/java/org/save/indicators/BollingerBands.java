package org.save.indicators;

import java.util.List;
import lombok.Data;
import org.save.indicators.utils.ListInitializer;
import org.save.indicators.utils.NumberFormatter;

@Data
public class BollingerBands {

  Integer period;

  List<Double> prices;

  // The middle line is equal to SMA(close)
  List<Double> basis;

  /*
     Moving average(SMA), shifted up by several standard deviations
     UPPER = BASE + (D * StdDev), where D is the number of standard deviations.
  */
  List<Double> upper;

  /*
     Moving average(SMA), shifted down by a few standard deviations
     LOWER = BASIS – (D * StdDev)
  */
  List<Double> lower;

  /*
     Standard deviation
     StdDev=SQRT( SUM((CLOSE - SMA(CLOSE, N))^2, N)/N )
  */
  List<Double> stdDev;

  List<Double> diff;

  public BollingerBands calculate(List<Double> prices, Integer period, Double numOfStanDev) {
    this.period = period;
    this.prices = prices;

    diff = ListInitializer.initOfZeroDouble(prices.size());
    upper = ListInitializer.initOfZeroDouble(prices.size());
    lower = ListInitializer.initOfZeroDouble(prices.size());
    stdDev = ListInitializer.initOfZeroDouble(prices.size());

    SimpleMovingAverage sma = new SimpleMovingAverage();
    basis = sma.calculate(prices, period).getResults();

    calculateStdDev();

    calculateUpperLine(numOfStanDev);
    calculateLowLine(numOfStanDev);

    return this;
  }

  // Calculate the standard deviations
  private void calculateStdDev() {
    Double sum = 0.0;

    for (int i = period - 1; i < prices.size(); i++) {

      sum = prices.subList(i - (period - 1), i + 1).stream().mapToDouble(doble -> doble).sum();
      sum = sum / period;

      Double stdDevRes = 0.0;
      for (int j = i - (period - 1); j <= i; j++) {
        stdDevRes = stdDevRes + Math.pow(prices.get(j) - sum, 2);
      }

      Double stdDevResSqrt = Math.sqrt(stdDevRes / period);
      stdDev.set(i, stdDevResSqrt);
    }
  }

  // UPPER = BASE + (D * StdDev)
  private void calculateUpperLine(Double indexD) {
    for (int i = period - 1; i < basis.size(); i++) {
      Double upperRes = basis.get(i) + (indexD * stdDev.get(i));
      upper.set(i, NumberFormatter.round(upperRes, 2));
    }
  }

  // LOWER = BASIS – (D * StdDev)
  private void calculateLowLine(Double indexD) {
    for (int i = period - 1; i < basis.size(); i++) {
      Double upperRes = basis.get(i) - (indexD * stdDev.get(i));
      lower.set(i, NumberFormatter.round(upperRes, 2));
    }
  }
}
