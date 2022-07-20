package org.save.service.implementation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.save.model.dto.financialmodelling.FinancialStatementDto;
import org.save.model.dto.financialmodelling.MonthFinancialStatementDto;
import org.save.util.TickerByCompanyName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FinancialStatementService {

  @Autowired private TickerByCompanyName tickerByCompanyName;

  public List<FinancialStatementDto> getStatFinancialStatement(String ticker) {
    List<FinancialStatementDto> list = new ArrayList<>();
    List<MonthFinancialStatementDto> financialStatement0 =
        tickerByCompanyName.getFinancialStatement(ticker);

    /*
    if we have 2-4 statements, then we don't calculate y/y
    if we have 1 statement, then we don't calculate q/q and y/y
    if we have 5 statements, then we can calculate q/q and y/y
     */
    if ((financialStatement0.size() > 1) && (financialStatement0.size() < 5)) {
      MonthFinancialStatementDto getFirstQuarter = financialStatement0.get(0);
      MonthFinancialStatementDto getBeforeQuarter = financialStatement0.get(1);
      return getFinancialStatements(list, getFirstQuarter, getBeforeQuarter);

    } else if (financialStatement0.size() == 1) {
      MonthFinancialStatementDto monthFinancialStatement = financialStatement0.get(0);
      list.add(
          new FinancialStatementDto(
              "Revenue", monthFinancialStatement.getRevenue(), monthFinancialStatement.getDate()));
      list.add(
          new FinancialStatementDto(
              "Net income",
              monthFinancialStatement.getNetIncome(),
              monthFinancialStatement.getDate()));
      list.add(
          new FinancialStatementDto(
              "Operating income",
              monthFinancialStatement.getOperatingIncome(),
              monthFinancialStatement.getDate()));
      list.add(
          new FinancialStatementDto(
              "Cash on hands",
              monthFinancialStatement.getTotalCash(),
              monthFinancialStatement.getDate()));
      list.add(
          new FinancialStatementDto(
              "Stockholders equity",
              monthFinancialStatement.getTotalShareholdersEquity(),
              monthFinancialStatement.getDate()));
      return list;

    } else if (financialStatement0.size() == 5) {
      MonthFinancialStatementDto firstQuarter = financialStatement0.get(0);
      MonthFinancialStatementDto beforeQuarter = financialStatement0.get(1);
      MonthFinancialStatementDto oneYearEgoQuarter = financialStatement0.get(4);

      return getFinancialStatements(list, firstQuarter, beforeQuarter, oneYearEgoQuarter);

    } else {
      return null;
    }
  }

  // without calculate y/y
  private List<FinancialStatementDto> getFinancialStatements(
      List<FinancialStatementDto> list,
      MonthFinancialStatementDto firstQuarter,
      MonthFinancialStatementDto beforeQuarter) {
    list.add(
        new FinancialStatementDto(
            "Revenue",
            firstQuarter.getRevenue(),
            getDiff(firstQuarter.getRevenue(), beforeQuarter.getRevenue()),
            null));
    list.add(
        new FinancialStatementDto(
            "Net income",
            firstQuarter.getNetIncome(),
            getDiff(firstQuarter.getNetIncome(), beforeQuarter.getNetIncome()),
            null));
    list.add(
        new FinancialStatementDto(
            "Operating income",
            firstQuarter.getOperatingIncome(),
            getDiff(firstQuarter.getOperatingIncome(), beforeQuarter.getOperatingIncome()),
            null));
    list.add(
        new FinancialStatementDto(
            "Cash on hands",
            firstQuarter.getTotalCash(),
            getDiff(firstQuarter.getTotalCash(), beforeQuarter.getTotalCash()),
            null));
    list.add(
        new FinancialStatementDto(
            "Stockholders equity",
            firstQuarter.getTotalShareholdersEquity(),
            getDiff(
                firstQuarter.getTotalShareholdersEquity(),
                beforeQuarter.getTotalShareholdersEquity()),
            null));
    return list;
  }

  // Calculate q/q and y/y
  private List<FinancialStatementDto> getFinancialStatements(
      List<FinancialStatementDto> list,
      MonthFinancialStatementDto firstQuarter,
      MonthFinancialStatementDto beforeQuarter,
      MonthFinancialStatementDto oneYearEgoQuarter) {
    list.add(
        new FinancialStatementDto(
            "Revenue",
            firstQuarter.getRevenue(),
            getDiff(firstQuarter.getRevenue(), beforeQuarter.getRevenue()),
            getDiff(firstQuarter.getRevenue(), oneYearEgoQuarter.getRevenue()),
            firstQuarter.getDate()));
    list.add(
        new FinancialStatementDto(
            "Net income",
            firstQuarter.getNetIncome(),
            getDiff(firstQuarter.getNetIncome(), beforeQuarter.getNetIncome()),
            getDiff(firstQuarter.getNetIncome(), oneYearEgoQuarter.getNetIncome()),
            firstQuarter.getDate()));
    list.add(
        new FinancialStatementDto(
            "Operating income",
            firstQuarter.getOperatingIncome(),
            getDiff(firstQuarter.getOperatingIncome(), beforeQuarter.getOperatingIncome()),
            getDiff(firstQuarter.getOperatingIncome(), oneYearEgoQuarter.getOperatingIncome()),
            firstQuarter.getDate()));
    list.add(
        new FinancialStatementDto(
            "Cash on hands",
            firstQuarter.getTotalCash(),
            getDiff(firstQuarter.getTotalCash(), beforeQuarter.getTotalCash()),
            getDiff(firstQuarter.getTotalCash(), oneYearEgoQuarter.getTotalCash()),
            firstQuarter.getDate()));
    list.add(
        new FinancialStatementDto(
            "Stockholders equity",
            firstQuarter.getTotalShareholdersEquity(),
            getDiff(
                firstQuarter.getTotalShareholdersEquity(),
                beforeQuarter.getTotalShareholdersEquity()),
            getDiff(
                firstQuarter.getTotalShareholdersEquity(),
                oneYearEgoQuarter.getTotalShareholdersEquity()),
            firstQuarter.getDate()));

    return list;
  }

  private BigDecimal getDiff(BigDecimal firstQuarter, BigDecimal beforeQuarter) {
    if (beforeQuarter.compareTo(new BigDecimal("0.0")) == 0) {
      return BigDecimal.ZERO;
    }
    return (firstQuarter.subtract(beforeQuarter))
        .divide(beforeQuarter.abs(), 3, RoundingMode.HALF_UP)
        .movePointRight(2);
  }
}
