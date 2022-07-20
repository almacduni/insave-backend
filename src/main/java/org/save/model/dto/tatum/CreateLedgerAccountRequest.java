package org.save.model.dto.tatum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class CreateLedgerAccountRequest {

  private String xpub;
  private String currency;
  private String accountingCurrency = "USD";
  private CustomerRegistration customer;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CustomerRegistration {

    String externalId;
  }

  public CreateLedgerAccountRequest(String xpub, String currency, CustomerRegistration customer) {
    this.xpub = xpub;
    this.currency = currency;
    this.customer = customer;
  }
}
