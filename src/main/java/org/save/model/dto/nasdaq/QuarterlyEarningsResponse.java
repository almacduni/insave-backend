package org.save.model.dto.nasdaq;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
public class QuarterlyEarningsResponse {
  private List<SurpriseAmount> surpriseAmount;
  private List<Forecast> forecast;
}
