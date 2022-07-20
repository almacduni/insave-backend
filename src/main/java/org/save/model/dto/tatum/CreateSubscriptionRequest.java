package org.save.model.dto.tatum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateSubscriptionRequest {

  private String type;
  private SubscriptionRequestAttribute attr;
}
