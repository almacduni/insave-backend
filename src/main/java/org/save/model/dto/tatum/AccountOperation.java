package org.save.model.dto.tatum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.save.model.enums.tatum.OperationType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountOperation implements Comparable<AccountOperation> {

  private Long time;
  private OperationType operationType;

  @Override
  public int compareTo(AccountOperation o) {
    return this.time.compareTo(o.getTime());
  }
}
