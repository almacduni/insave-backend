package org.save.indicators;

import java.util.List;
import lombok.Data;
import org.save.indicators.utils.ListInitializer;
import org.save.indicators.utils.NumberFormatter;

@Data
public class TradersDynamicIndex {

  // Standard deviation for the BB indicator
  private static final Double numOfStandardDev = 1.6185;

  Integer rsiPeriod;
  Integer fastSmaPeriod;
  Integer slowSmaPeriod;
  Integer bbPeriod;

  List<Double> prices;

  // Relative Strength Index
  List<Double> rsi;

  // SMA(rsi, fastPeriod)
  List<Double> fastSma;

  // SMA(rsi, slowPeriod)
  List<Double> slowSma;

  // BB(rsi, bbPeriod) upper line
  List<Double> bbUpper;

  // BB(rsi, bbPeriod) lower line
  List<Double> bbLower;

  // (bbUpper + bbLower) / 2
  List<Double> bbMiddle;

  public TradersDynamicIndex calculate(
      List<Double> prices,
      Integer rsiPeriod,
      Integer fastSmaPeriod,
      Integer slowSmaPeriod,
      Integer bbPeriod) {

    this.prices = prices;
    this.rsiPeriod = rsiPeriod;
    this.fastSmaPeriod = fastSmaPeriod;
    this.slowSmaPeriod = slowSmaPeriod;
    this.bbPeriod = bbPeriod;

    calculateRsi();

    calculateSmaPeriods();

    calculateBb();

    return this;
  }

  private void calculateRsi() {
    RelativeStrengthIndex rsiIndicator = new RelativeStrengthIndex();
    rsi = rsiIndicator.calculate(prices, rsiPeriod).getRsi();
    rsi.subList(0, rsiPeriod).clear();
  }

  private void calculateSmaPeriods() {
    SimpleMovingAverage smaIndicator = new SimpleMovingAverage();
    fastSma = smaIndicator.calculate(rsi, fastSmaPeriod).getResults();
    slowSma = smaIndicator.calculate(rsi, slowSmaPeriod).getResults();
  }

  private void calculateBb() {
    BollingerBands bbIndicator = new BollingerBands();
    bbIndicator.calculate(rsi, bbPeriod, numOfStandardDev);
    bbUpper = bbIndicator.getUpper();
    bbLower = bbIndicator.getLower();

    bbMiddle = ListInitializer.initOfZeroDouble(bbUpper.size());

    for (int i = bbPeriod - 1; i < bbMiddle.size(); i++) {
      Double bbMiddleRes = (bbLower.get(i) + bbUpper.get(i)) / 2;
      bbMiddle.set(i, NumberFormatter.round(bbMiddleRes, 2));
    }
  }
}
