package org.save.model.dto.coinmarketcap;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CryptoTickerDto {

  private Long id;

  @JsonAlias("slug")
  private String companyName;

  @JsonAlias("symbol")
  private String name;
}
