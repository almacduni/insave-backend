package org.save.model.dto.tatum;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.save.model.enums.CryptoCurrency;
import org.save.model.enums.OrderType;

@Data
@AllArgsConstructor
public class CryptoTradeRequest {

  private Long userId;
  private OrderType orderType;
  private String price;
  private BigDecimal amount;
  private CryptoCurrency firstCurrency;
  private CryptoCurrency secondCurrency;
  private Boolean isPriceSet;
}
