package org.save.util.mapper.indicators;

import org.save.indicators.MovingAverageConvergenceDivergence;
import org.save.model.dto.indicators.MACDIndicatorResponse;
import org.springframework.stereotype.Component;

@Component
public class MACDIndicatorMapper {

  public MACDIndicatorResponse convertToMACDIndResponse(
      MovingAverageConvergenceDivergence indicator) {
    return new MACDIndicatorResponse(indicator.getMacd(), indicator.getSignal());
  }
}
