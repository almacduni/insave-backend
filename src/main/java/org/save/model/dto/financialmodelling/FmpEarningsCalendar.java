package org.save.model.dto.financialmodelling;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FmpEarningsCalendar {
  private String date;
  private BigDecimal epsEstimated;

  @JsonAlias("eps")
  private BigDecimal epsReported;
}
