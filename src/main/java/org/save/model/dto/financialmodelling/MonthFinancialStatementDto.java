package org.save.model.dto.financialmodelling;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@Component
public class MonthFinancialStatementDto {

  private BigDecimal revenue;
  private BigDecimal operatingIncome;
  private BigDecimal netIncome;
  private BigDecimal totalCash;
  private BigDecimal totalShareholdersEquity;
  private String date;

  private Map<String, BigDecimal> ratioOfTheCurrentQuarterToThePrevious;
  private Map<String, BigDecimal> ratioOfTheCurrentQuarterToLastYear;

  public MonthFinancialStatementDto(
      BigDecimal revenue,
      BigDecimal operatingIncome,
      BigDecimal netIncome,
      BigDecimal totalCash,
      BigDecimal totalShareholdersEquity) {
    this.revenue = revenue;
    this.operatingIncome = operatingIncome;
    this.netIncome = netIncome;
    this.totalCash = totalCash;
    this.totalShareholdersEquity = totalShareholdersEquity;
  }

  public MonthFinancialStatementDto(
      BigDecimal revenue,
      BigDecimal operatingIncome,
      BigDecimal netIncome,
      BigDecimal totalCash,
      BigDecimal totalShareholdersEquity,
      String date) {
    this.revenue = revenue;
    this.operatingIncome = operatingIncome;
    this.netIncome = netIncome;
    this.totalCash = totalCash;
    this.totalShareholdersEquity = totalShareholdersEquity;
    this.date = date;
  }

  public MonthFinancialStatementDto(
      BigDecimal revenue, BigDecimal operatingIncome, BigDecimal netIncome) {
    this.revenue = revenue;
    this.operatingIncome = operatingIncome;
    this.netIncome = netIncome;
  }

  public MonthFinancialStatementDto(BigDecimal totalCash, BigDecimal totalShareholdersEquity) {
    this.totalCash = totalCash;
    this.totalShareholdersEquity = totalShareholdersEquity;
  }
}
