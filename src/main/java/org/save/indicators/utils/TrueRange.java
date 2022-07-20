package org.save.indicators.utils;

public class TrueRange {

  // The true range shows the maximum spread of prices for transactions starting from the closing of
  // the previous period.
  public static Double calculate(double high, double low, double previousClose) {
    double value1 = high - low;
    double value2 = Math.abs(high - previousClose);
    double value3 = Math.abs(low - previousClose);
    return NumberFormatter.round(Math.max(value1, Math.max(value2, value3)), 3);
  }
}
