package org.save.indicators.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListInitializer {

  public static List<Double> initOfZeroDouble(int size) {
    return new ArrayList<>(Collections.nCopies(size, 0.0));
  }

  public static List<Integer> initOfZeroInt(int size) {
    return new ArrayList<>(Collections.nCopies(size, 0));
  }
}
