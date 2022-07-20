package org.save.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.save.model.enums.Period;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class IndicatorConfig {

  @Bean
  @RequestScope
  public Map<Integer, List<Double>> emaIndicators(List<Period> periods) {
    Map<Integer, List<Double>> emaIndicators = new HashMap();
    periods.forEach((key -> emaIndicators.put(key.getTimeFrame(), new ArrayList<>())));
    return emaIndicators;
  }

  @Bean
  public List<Period> periods() {
    return List.of(
        Period.THREE,
        Period.FIVE,
        Period.EIGHT,
        Period.TEN,
        Period.TWELVE,
        Period.FIFTEEN,
        Period.THIRTY,
        Period.THIRTYFIVE,
        Period.FORTY,
        Period.FORTYFIVE,
        Period.FIFTY,
        Period.SIXTY);
  }
}
