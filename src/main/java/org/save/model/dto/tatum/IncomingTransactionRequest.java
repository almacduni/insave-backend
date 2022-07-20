package org.save.model.dto.tatum;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class IncomingTransactionRequest {

  private BigDecimal amount;
  private String currency;
  private String txId;

  @JsonProperty("to")
  private String address;
}
