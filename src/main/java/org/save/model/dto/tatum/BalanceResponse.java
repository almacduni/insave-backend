package org.save.model.dto.tatum;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BalanceResponse extends LedgerAccountDto {

  private String address;
}
