package org.save.model.dto.nasdaq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
public class SurpriseAmount {
  private String fiscalQuarterEnd;
  private String dateReported;
  private String earningsPerShare;
  private String consensusEPSForecast;
}
