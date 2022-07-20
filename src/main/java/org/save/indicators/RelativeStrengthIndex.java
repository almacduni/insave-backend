package org.save.indicators;

import java.util.List;
import lombok.Data;
import org.save.indicators.utils.ListInitializer;
import org.save.indicators.utils.NumberFormatter;

@Data
public class RelativeStrengthIndex {

  private List<Double> prices;
  private int period;

  // Difference between the closing prices of the current and previous days
  private List<Double> change;

  // Rising days (The closing price of the current day is higher than the last)
  private List<Double> gain;

  // Descending days (The closing price of the current day is lower than the last one)
  private List<Double> loss;

  // Avg. Positive price / Avg. Negative price (for the specified period)
  private List<Double> rs;

  private List<Double> rsi;

  // Avg. Positive price
  private List<Double> avgGain;

  // Avg. Negative price
  private List<Double> avgLoss;

  public RelativeStrengthIndex calculate(List<Double> prices, int period) {
    this.prices = prices;
    this.period = period;

    change = ListInitializer.initOfZeroDouble(prices.size());

    gain = ListInitializer.initOfZeroDouble(prices.size());
    loss = ListInitializer.initOfZeroDouble(prices.size());

    avgGain = ListInitializer.initOfZeroDouble(prices.size());
    avgLoss = ListInitializer.initOfZeroDouble(prices.size());

    rs = ListInitializer.initOfZeroDouble(prices.size());
    rsi = ListInitializer.initOfZeroDouble(prices.size());

    for (int i = 0; i < this.prices.size(); i++) {

      if (i > 0) {
        difference(i);
      }

      if (i == period) {
        calculateTheFirstAverage(i);

      } else if (i >= this.period) {
        calculateTheRestAverage(i);
      }

      if (i >= this.period) {

        // RS = Average Gain / Average Loss
        Double rsResult = avgGain.get(i) / avgLoss.get(i);
        rs.set(i, rsResult);

        //               100
        // RSI = 100 - --------
        //              1 + RS

        Double rsiResult = NumberFormatter.round(100 - (100 / (1 + rs.get(i))), 2);
        rsi.set(i, rsiResult);
      }
    }
    return this;
  }

  // Find the difference between the closing prices of the current and last days
  private void difference(int index) {
    change.set(index, prices.get(index) - prices.get(index - 1));
    if (change.get(index) > 0) {
      gain.set(index, change.get(index));
    } else if (change.get(index) < 0) {
      loss.set(index, Math.abs(change.get(index)));
    }
  }

  // Calculate the first average of positive and negative changes in the closing price
  private void calculateTheFirstAverage(int index) {
    Double avgGainResult =
        gain.subList(0, period).stream().mapToDouble(doubl -> doubl).average().getAsDouble();
    avgGain.set(index, avgGainResult);

    Double avgLosResult =
        loss.subList(0, period).stream().mapToDouble(doubl -> doubl).average().getAsDouble();
    avgLoss.set(index, avgLosResult);
  }

  // Calculate the average of positive and negative changes in the closing price for the value index
  // > period
  private void calculateTheRestAverage(int index) {
    Double avgGainResult = (avgGain.get(index - 1) * (period - 1) + gain.get(index)) / period;
    avgGain.set(index, avgGainResult);

    Double avgLossResult = (avgLoss.get(index - 1) * (period - 1) + loss.get(index)) / period;
    avgLoss.set(index, avgLossResult);
  }
}
