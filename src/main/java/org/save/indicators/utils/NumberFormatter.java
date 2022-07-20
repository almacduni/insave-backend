package org.save.indicators.utils;

import java.math.BigDecimal;

public class NumberFormatter {

  public static double round(double value, int numberOfDigitsAfterDecimalPoint) {
    BigDecimal bigDecimal = new BigDecimal(value);
    bigDecimal = bigDecimal.setScale(numberOfDigitsAfterDecimalPoint, BigDecimal.ROUND_HALF_UP);
    return bigDecimal.doubleValue();
  }
}
