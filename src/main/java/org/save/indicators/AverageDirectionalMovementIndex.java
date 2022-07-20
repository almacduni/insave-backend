package org.save.indicators;

import java.util.List;
import lombok.Data;
import org.save.indicators.utils.ListInitializer;
import org.save.indicators.utils.NumberFormatter;
import org.save.indicators.utils.TrueRange;

@Data
public class AverageDirectionalMovementIndex {

  // Maximum prices of bars
  private List<Double> high;

  // Minimum prices of bars
  private List<Double> low;

  // Closing prices of bars
  private List<Double> close;

  private int period;

  // The true range showing the maximum spread of prices for transactions
  private List<Double> tr;

  // True range for the period
  private List<Double> trPeriod;

  // Absolutely positive price movements
  private List<Double> posDM1;

  // Absolutely negative price movements
  private List<Double> negDM1;

  // Absolutely positive price movements for the period
  private List<Double> posDmPeriod;

  // Absolutely negative price movements for the period
  private List<Double> negDmPeriod;

  // Positive directional indicator for the period (+DI)
  private List<Double> posDiPeriod;

  // Negative directional indicator for the period (-DI)
  private List<Double> negDiPeriod;

  // +DI - -DI
  private List<Double> diDiffPeriod;

  // +DI + -DI
  private List<Double> diSumPeriod;

  // DX
  private List<Double> dx;

  // ADX
  private List<Double> adx;

  // Calculate ADX (Average Directional Index)
  public AverageDirectionalMovementIndex calculate(
      List<Double> high, List<Double> low, List<Double> close, int period) {
    this.high = high;
    this.low = low;
    this.close = close;
    this.period = period;

    tr = ListInitializer.initOfZeroDouble(high.size());
    posDM1 = ListInitializer.initOfZeroDouble(high.size());
    negDM1 = ListInitializer.initOfZeroDouble(high.size());

    trPeriod = ListInitializer.initOfZeroDouble(high.size());

    posDmPeriod = ListInitializer.initOfZeroDouble(high.size());
    negDmPeriod = ListInitializer.initOfZeroDouble(high.size());

    posDiPeriod = ListInitializer.initOfZeroDouble(high.size());
    negDiPeriod = ListInitializer.initOfZeroDouble(high.size());

    diDiffPeriod = ListInitializer.initOfZeroDouble(high.size());
    diSumPeriod = ListInitializer.initOfZeroDouble(high.size());

    dx = ListInitializer.initOfZeroDouble(high.size());
    adx = ListInitializer.initOfZeroDouble(high.size());

    for (int i = 0; i < high.size(); i++) {

      if (i > 0) {

        // Calc True Range
        Double trResult = TrueRange.calculate(high.get(i), low.get(i), close.get(i - 1));
        tr.set(i, trResult);

        // Calc +DM1
        Double posDMRes =
            positiveDirectionalMovement(high.get(i), high.get(i - 1), low.get(i), low.get(i - 1));
        posDM1.set(i, posDMRes);

        // Calc -DM1
        Double negDMRes =
            negativeDirectionalMovement(high.get(i), high.get(i - 1), low.get(i), low.get(i - 1));
        negDM1.set(i, negDMRes);
      }

      trueRangePeriod(i);

      positiveDirectionalMovementPeriod(i);
      negativeDirectionalMovementPeriod(i);

      positiveDirectionalIndicator(i);
      negativeDirectionalIndicator(i);

      directionalDiffPeriod(i);
      directionalSumPeriod(i);

      directionalIndex(i);
      averageDirectionalIndex(i);
    }

    return this;
  }

  /*
   Calculate Positive Directional Movement

   Directional movement is positive (plus) when the current high minus the
   prior high is greater than the prior low minus the current low.
  */
  private Double positiveDirectionalMovement(
      Double high, Double previousHigh, Double low, Double previousLow) {
    if ((high - previousHigh) > (previousLow - low)) {
      return NumberFormatter.round(Math.max((high - previousHigh), 0), 3);
    } else {
      return 0.0;
    }
  }

  /*
   Calculate Negative Directional Movement

   Directional movement is negative (minus) when the prior low minus the
   current low is greater than the current high minus the prior high.
  */
  private Double negativeDirectionalMovement(
      Double high, Double previousHigh, Double low, Double previousLow) {
    if ((previousLow - low) > (high - previousHigh)) {
      return NumberFormatter.round(Math.max((previousLow - low), 0), 3);
    } else {
      return 0.0;
    }
  }

  // Calculate the true range showing the maximum spread of prices for transactions
  private void trueRangePeriod(int idx) {
    if (idx == period) {

      Double sum = tr.subList(1, period + 1).stream().mapToDouble(doble -> doble).sum();
      trPeriod.set(idx, NumberFormatter.round(sum, 3));

    } else if (idx > period) {

      // Use previous TR Period to build on current TR Period
      Double prevTrPeriod = trPeriod.get(idx - 1);
      Double trPeriodRes =
          NumberFormatter.round(prevTrPeriod - (prevTrPeriod / period) + tr.get(idx), 3);
      trPeriod.set(idx, trPeriodRes);

    } else {

      // Before Period, no calc required set to 0
      trPeriod.set(idx, 0.0);
    }
  }

  // Find an absolutely positive price movement for the period
  private void positiveDirectionalMovementPeriod(int idx) {
    Double posDmPeriodRes;

    if (idx == period) {

      // Determine First value of the given period
      posDmPeriodRes = posDM1.subList(0, period + 1).stream().mapToDouble(doble -> doble).sum();
      posDmPeriod.set(idx, NumberFormatter.round(posDmPeriodRes, 3));

    } else if (idx > period) {

      // Determine remaining values beyond the first value
      double prevPosDmPeriod = posDmPeriod.get(idx - 1);
      posDmPeriodRes =
          NumberFormatter.round(prevPosDmPeriod - (prevPosDmPeriod / period) + posDM1.get(idx), 3);
      posDmPeriod.set(idx, posDmPeriodRes);

    } else {

      // values for less then the given period
      posDmPeriod.set(idx, 0.0);
    }
  }

  // Find an absolutely negative price movement for the period
  private void negativeDirectionalMovementPeriod(int idx) {
    Double negDmPeriodRes;

    if (idx == period) {
      // Determine First value of the given period
      negDmPeriodRes = negDM1.subList(0, period + 1).stream().mapToDouble(doble -> doble).sum();
      negDmPeriod.set(idx, NumberFormatter.round(negDmPeriodRes, 3));

    } else if (idx > this.period) {
      // Determine remaining values beyond the first value
      double prevNegDmPeriod = negDmPeriod.get(idx - 1);
      negDmPeriodRes =
          NumberFormatter.round(prevNegDmPeriod - (prevNegDmPeriod / period) + negDM1.get(idx), 3);
      negDmPeriod.set(idx, negDmPeriodRes);

    } else {
      // values for less then the given period
      negDmPeriod.set(idx, 0.0);
    }
  }

  /*
   Positive directional indicator
   +DI = +DM / TR
  */
  private void positiveDirectionalIndicator(int idx) {

    Double posDmPer = posDmPeriod.get(idx);
    Double trPer = trPeriod.get(idx);

    if (trPer > 0.0) {
      posDiPeriod.set(idx, NumberFormatter.round(100 * (posDmPer / trPer), 3));
    } else {
      posDiPeriod.set(idx, 0.0);
    }
  }

  /*
  Negative directional indicator
  -DI = -DM / TR
  */
  private void negativeDirectionalIndicator(int idx) {
    Double negDmPer = negDmPeriod.get(idx);
    Double trPer = trPeriod.get(idx);

    if (trPer > 0) {
      negDiPeriod.set(idx, NumberFormatter.round(100 * (negDmPer / trPer), 3));
    } else {
      negDiPeriod.set(idx, 0.0);
    }
  }

  // +DI - -DI
  private void directionalDiffPeriod(int idx) {
    Double dirDiffPerRes =
        NumberFormatter.round(Math.abs(posDiPeriod.get(idx) - negDiPeriod.get(idx)), 3);
    diDiffPeriod.set(idx, dirDiffPerRes);
  }

  // +DI + -DI
  private void directionalSumPeriod(int idx) {
    Double dirSumPerRes =
        NumberFormatter.round(Math.abs(posDiPeriod.get(idx) + negDiPeriod.get(idx)), 3);
    diSumPeriod.set(idx, dirSumPerRes);
  }

  private void directionalIndex(int idx) {
    double diDiffPer = diDiffPeriod.get(idx);
    double diSumPer = diSumPeriod.get(idx);

    if (diSumPer > 0.0) {
      Double dxRes = NumberFormatter.round(100 * (diDiffPer / diSumPer), 3);
      dx.set(idx, dxRes);
    } else {
      dx.set(idx, 0.0);
    }
  }

  // ADX
  private void averageDirectionalIndex(int idx) {
    Double adiRes;

    if (idx == period) {
      adiRes = dx.subList(0, period).stream().mapToDouble(doble -> doble).average().getAsDouble();
      adx.set(idx, NumberFormatter.round(adiRes, 3));

    } else if (idx > period) {
      adiRes = (adx.get(idx - 1) * (period - 1) + dx.get(idx)) / period;
      adx.set(idx, NumberFormatter.round(adiRes, 3));
    } else {
      adx.set(idx, 0.0);
    }
  }
}
