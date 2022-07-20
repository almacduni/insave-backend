package org.save.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.SneakyThrows;

public class DateParseUtils {

  public static final String PATTERN_FOR_CANDLES = "yyyy-MM-dd HH:mm";

  @SneakyThrows
  public static String simpleFormatterDate(String date, String formatter) {
    if (!date.isEmpty() || date != null || !date.equals("null")) {
      SimpleDateFormat simpleFormatter = new SimpleDateFormat(formatter);
      Date resultDate = simpleFormatter.parse(date);
      return simpleFormatter.format(resultDate);
    }
    return null;
  }
}
