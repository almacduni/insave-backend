package org.save.model.dto.portfolio;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class PerformanceHistoryItemDto {

  private String date;
  private BigDecimal price;
}
