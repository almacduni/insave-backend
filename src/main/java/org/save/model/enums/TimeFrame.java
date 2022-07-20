package org.save.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TimeFrame {
  ONE_MINUTE("1/minute"),
  FIVE_MINUTES("5/minute"),
  THIRTY_MINUTES("30/minute"),
  ONE_HOUR("60/minute"),
  FOUR_HOUR("240/minute"),
  ONE_WEEK("1/week"),
  ONE_DAY("1/day"),
  ONE_MONTH("1/month"),
  ONE_YEAR("1/year");

  @Getter private final String timeFrame;
}
