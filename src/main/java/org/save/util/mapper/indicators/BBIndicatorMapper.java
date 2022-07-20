package org.save.util.mapper.indicators;

import org.save.indicators.BollingerBands;
import org.save.model.dto.indicators.BollingerBandsIndicatorResponse;
import org.springframework.stereotype.Component;

@Component
public class BBIndicatorMapper {

  public BollingerBandsIndicatorResponse convertToBBResponse(BollingerBands indicator) {
    return new BollingerBandsIndicatorResponse(
        indicator.getBasis(), indicator.getUpper(), indicator.getLower());
  }
}
