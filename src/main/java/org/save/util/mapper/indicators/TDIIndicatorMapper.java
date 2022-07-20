package org.save.util.mapper.indicators;

import org.save.indicators.TradersDynamicIndex;
import org.save.model.dto.indicators.TradersDynamicIndexResponse;
import org.springframework.stereotype.Component;

@Component
public class TDIIndicatorMapper {

  public TradersDynamicIndexResponse convertToTDIResponse(TradersDynamicIndex index) {
    return new TradersDynamicIndexResponse(
        index.getFastSma(),
        index.getSlowSma(),
        index.getBbUpper(),
        index.getBbLower(),
        index.getBbMiddle());
  }
}
