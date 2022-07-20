package org.save.model.dto.tatum;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class AccountBalanceDto {

  private BigDecimal accountBalance;
  private BigDecimal availableBalance;
}
