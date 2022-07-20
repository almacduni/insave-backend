package org.save.model.dto.indicators;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BollingerBandsIndicatorResponse {

  List<Double> basis;
  List<Double> upper;
  List<Double> lower;
}
