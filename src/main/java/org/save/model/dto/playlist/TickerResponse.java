package org.save.model.dto.playlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TickerResponse {

  private String name;
  private String company;
  private String marketCapitalization;
  private Double amg;
}
