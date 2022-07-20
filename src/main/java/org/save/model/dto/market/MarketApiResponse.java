package org.save.model.dto.market;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketApiResponse<T> {
  private T data;
  private CurrentCandleDto current;
  private String message;
  private int resultCode;
}
