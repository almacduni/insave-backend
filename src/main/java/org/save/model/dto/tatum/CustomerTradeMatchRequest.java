package org.save.model.dto.tatum;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CustomerTradeMatchRequest {

  private BigDecimal amount;
  private BigDecimal price;
  private String type;
  private String pair;
  private String currency1AccountId;
  private String currency2AccountId;

  @JsonProperty("currency")
  private String CryptoCurrency;
}
