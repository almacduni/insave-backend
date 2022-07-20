package org.save.model.dto.tatum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LedgerAccountDto {

  private String id;
  private String currency;
  private String customerId;
  private String xpub;
  private AccountBalanceDto balance;
  private Boolean frozen;
  private Boolean active;
}
