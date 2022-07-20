package org.save.model.dto.financialmodelling;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinancialStatementDto {

  private String title;
  private BigDecimal current;
  private BigDecimal qq;
  private BigDecimal yy;
  private String date;

  public FinancialStatementDto(String title, BigDecimal current, BigDecimal qq, BigDecimal yy) {
    this.title = title;
    this.current = current;
    this.qq = qq;
    this.yy = yy;
  }

  public FinancialStatementDto(String title, BigDecimal current, String date) {
    this.title = title;
    this.current = current;
    this.date = date;
  }
}
