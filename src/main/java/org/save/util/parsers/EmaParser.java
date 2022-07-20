package org.save.util.parsers;

import java.util.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.save.indicators.MultipleMovingAverage;
import org.save.model.enums.Period;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmaParser {

  @Getter private final Map<Integer, List<Double>> emaIndicators;
  private final List<Period> periods;

  public void addAveragesToEmaIndicators(MultipleMovingAverage averages) {
    // checking the presence of a group. If averages is less than three, then there are no groups
    periods.forEach(period -> checkExistAvgAndAddEmaIndicator(period, averages));
  }

  public void checkExistAvgAndAddEmaIndicator(Period period, MultipleMovingAverage averages) {
    if (averages.longestOf(period.getGroup()) != null) {
      if (averages.longestOf(period.getGroup()).getTimeFrame() >= period.getTimeFrame()) {
        emaIndicators
            .get(period.getTimeFrame())
            .add(
                Double.parseDouble(
                    String.format(Locale.ENGLISH, "%.2f", averages.getValue(period).toDouble())));
      } else {
        emaIndicators.get(period.getTimeFrame()).add((double) 0);
      }
    } else { // if there is no LONGTERM
      emaIndicators.get(period.getTimeFrame()).add((double) 0);
    }
  }

  public Map<Integer, Double> getEmaIndicatorWithoutList() {
    Map<Integer, Double> emaIndicator = new HashMap<>();
    periods.forEach(
        key -> emaIndicator.put(key.getTimeFrame(), emaIndicators.get(key.getTimeFrame()).get(0)));
    return emaIndicator;
  }
}
