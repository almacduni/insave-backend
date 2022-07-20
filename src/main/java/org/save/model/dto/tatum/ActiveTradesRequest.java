package org.save.model.dto.tatum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveTradesRequest {

  private String customerId;
  private Integer pageSize;
  private Integer offset;
  private String orderType;
  private String pair;
}
