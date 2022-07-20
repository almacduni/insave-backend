package org.save.model.dto.tatum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.save.model.enums.OrderType;

@Data
@AllArgsConstructor
@Builder
public class CreateCryptoTradeRequest {

  OrderType type;
  String price;
  String amount;
  String pair;
  String currency1AccountId;
  String currency2AccountId;
}
