package org.save.indicators;

import com.google.common.collect.Maps;
import eu.verdelhan.ta4j.TADecimal;
import java.util.*;
import org.save.model.enums.Group;
import org.save.model.enums.Period;
import org.save.service.implementation.IndicatorService;

public class MultipleMovingAverage {

  // Total number of moving averages
  private static final int MAP_SIZE = 12;

  // Map sorted by the period's time frame
  private Map<Period, TADecimal> objectMap =
      new TreeMap<>(Comparator.comparingInt(Period::getTimeFrame));

  public void setValue(Period period, TADecimal value) {
    objectMap.put(period, value);
  }

  /**
   * Determines the shortest period for this multiple moving average.
   *
   * @param group The group to determine the shortest period for.
   * @return The shortest period. May be null if the group could not be determined
   */
  public Period shortestOf(Group group) {
    // Create a list from the map, filtered by group and return the first entry (shortest period)
    List<Period> periods =
        new ArrayList<>(
            Maps.filterEntries(objectMap, IndicatorService.groupPredicate(group)).keySet());
    return periods.get(0);
  }

  /**
   * Determines the longest period for this multiple moving average.
   *
   * @param group The group to determine the longest period for.
   * @return The longest period. May be null if the group could not be determined
   */
  public Period longestOf(Group group) {
    // Create a list from the map, filtered by group and return the last entry (longest period)
    List<Period> periods =
        new ArrayList<>(
            Maps.filterEntries(objectMap, IndicatorService.groupPredicate(group)).keySet());
    if (periods.size() == 0) {
      return null;
    }
    return periods.get(periods.size() - 1);
  }

  /**
   * @param period A single period for the required value
   * @return A value for the specified period
   */
  public TADecimal getValue(Period period) {
    return objectMap.get(period);
  }

  /**
   * Determines the lowest value from a given group
   *
   * @param group - The group to determine the lowest value from
   * @return The lowest period for the supplied group or null, if none could be determined
   */
  public Period lowestOf(Group group) {

    Period lowest = null;
    TADecimal lowestValue = null;

    for (Map.Entry<Period, TADecimal> entry : objectMap.entrySet()) {
      Period period = entry.getKey();
      if (period.getGroup().equals(group)) {
        if (lowest == null || objectMap.get(period).isLessThan(lowestValue)) {
          lowest = period;
          lowestValue = objectMap.get(period);
        }
      }
    }
    return lowest;
  }

  /**
   * Determines the highest value from a given group
   *
   * @param group - The group to determine the highest value from
   * @return The highest period for the supplied group or null, if none could be determined
   */
  public Period highestOf(Group group) {

    Period highest = null;
    TADecimal highestValue = null;

    for (Map.Entry<Period, TADecimal> entry : objectMap.entrySet()) {
      Period period = entry.getKey();
      if (period.getGroup().equals(group)) {
        if (highest == null || objectMap.get(period).isGreaterThan(highestValue)) {
          highest = period;
          highestValue = objectMap.get(period);
        }
      }
    }

    return highest;
  }

  /** @return All period values */
  public List<TADecimal> getValues() {
    List<TADecimal> values = new ArrayList<>();
    objectMap.forEach((key, value) -> values.add(getValue(key)));
    return values;
  }

  /** @return A list of values within the specified group */
  public List<TADecimal> getValues(Group group) {
    List<TADecimal> values = new ArrayList<>();
    objectMap.forEach(
        (period, value) -> {
          if (period.getGroup().equals(group)) {
            values.add(getValue(period));
          }
        });
    return values;
  }

  /**
   * A conveinence method that determines if this instance of the mma is complete. A complete mma
   * object is one where all period values are non-null which, by definition is at least n-samples
   * of input data, where n is the largest period
   *
   * @return Whether the mma is complete. In most cases, only a complete mma should be used
   */
  public boolean isComplete() {
    // Ensure values defined for a period are not null and that the total map size is expected
    for (Map.Entry<Period, TADecimal> entry : objectMap.entrySet()) {
      Period period = entry.getKey();
      if (objectMap.get(period) == null) {
        return false;
      }
    }
    return objectMap.size() == MAP_SIZE;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Period period : objectMap.keySet()) {
      builder.append(period + ": " + objectMap.get(period) + " ");
    }
    return builder.toString();
  }
}
