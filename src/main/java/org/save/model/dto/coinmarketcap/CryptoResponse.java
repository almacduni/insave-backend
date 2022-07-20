package org.save.model.dto.coinmarketcap;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CryptoResponse {

  private Set<CryptoTickerDto> data;
}
