package org.save.model.dto.indicators;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradersDynamicIndexResponse {

  List<Double> fastSma;
  List<Double> slowSma;
  List<Double> bbUpper;
  List<Double> bbLower;
  List<Double> bbMiddle;
}
