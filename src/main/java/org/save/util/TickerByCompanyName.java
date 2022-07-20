package org.save.util;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.save.model.dto.financialmodelling.EarningSurpriseDto;
import org.save.model.dto.financialmodelling.MonthFinancialStatementDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TickerByCompanyName {

  @Value("${insave.integration.finances.financial_modelling.api_key}")
  private String API_KEY;

  public List<EarningSurpriseDto> getEarningSurprise(String ticker) {
    EarningSurpriseDto earningSurprise;
    List<EarningSurpriseDto> list = new ArrayList<>();

    JSONArray json = null;
    try {
      json =
          readJsonFromUrl(
              "https://financialmodelingprep.com/api/v3/earnings-surpises/"
                  + ticker.toUpperCase()
                  + "?apikey="
                  + API_KEY);
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (json != null) {
      JSONArray jsonArray = new JSONArray(json);
      for (int i = 0; i < 1; i++) {
        earningSurprise =
            new EarningSurpriseDto(
                jsonArray.getJSONObject(i).getString("date"),
                jsonArray.getJSONObject(i).getString("symbol"),
                jsonArray.getJSONObject(i).getBigDecimal("actualEarningResult"),
                jsonArray.getJSONObject(i).getBigDecimal("estimatedEarning"));

        list.add(earningSurprise);
      }
    } else {
      earningSurprise =
          new EarningSurpriseDto("null", "null", new BigDecimal("0"), new BigDecimal("0"));

      list.add(earningSurprise);
    }
    return list;
  }

  public List<MonthFinancialStatementDto> getFinancialStatement(String ticker) {
    MonthFinancialStatementDto financialStatement;
    List<MonthFinancialStatementDto> list = new ArrayList<>();
    JSONArray jsonIncomeStatement = null;
    JSONArray jsonBalanceSheetStatement = null;
    try {
      jsonIncomeStatement =
          readJsonFromUrl(
              "https://financialmodelingprep.com/api/v3/income-statement/"
                  + ticker.toUpperCase()
                  + "?period=quarter&limit=400&apikey="
                  + API_KEY);
      jsonBalanceSheetStatement =
          readJsonFromUrl(
              "https://financialmodelingprep.com/api/v3/balance-sheet-statement/"
                  + ticker.toUpperCase()
                  + "?period=quarter&limit=400&apikey="
                  + API_KEY
                  + "&limit=120");
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (jsonBalanceSheetStatement == null && jsonIncomeStatement == null) {
      return null;
    }
    if (jsonIncomeStatement != null && jsonBalanceSheetStatement != null) {
      if (jsonBalanceSheetStatement.length() < 5 && jsonIncomeStatement.length() < 5) {
        return receivingIncompleteInformation(jsonBalanceSheetStatement, jsonIncomeStatement);
      }
      for (int i = 0; i < 5; i++) {
        financialStatement =
            initMonthFinancialStatementWithDate(
                jsonIncomeStatement.getJSONObject(i), jsonBalanceSheetStatement.getJSONObject(i));
        list.add(financialStatement);
      }
    } else {
      list.add(
          new MonthFinancialStatementDto(
              new BigDecimal("0"),
              new BigDecimal("0"),
              new BigDecimal("0"),
              new BigDecimal("0"),
              new BigDecimal("0")));
    }
    return list;
  }

  private List<MonthFinancialStatementDto> receivingIncompleteInformation(
      JSONArray jsonBalanceSheetStatement, JSONArray jsonIncomeStatement) {
    MonthFinancialStatementDto financialStatement;
    List<MonthFinancialStatementDto> list = new ArrayList<>();

    if (jsonBalanceSheetStatement.length() < jsonIncomeStatement.length()) {
      for (int i = 0; i < jsonIncomeStatement.length(); i++) {
        if (i < 2) {
          financialStatement =
              initMonthFinancialStatement(
                  jsonIncomeStatement.getJSONObject(i), jsonBalanceSheetStatement.getJSONObject(i));
        } else {
          financialStatement = initMonthFinancialStatement(jsonIncomeStatement.getJSONObject(i));
        }
        list.add(financialStatement);
      }
    } else {
      for (int i = 0; i < jsonIncomeStatement.length(); i++) {
        if (jsonIncomeStatement.length() == 1) {
          financialStatement =
              initMonthFinancialStatementWithDate(
                  jsonIncomeStatement.getJSONObject(i), jsonBalanceSheetStatement.getJSONObject(i));
          financialStatement.setDate(
              Arrays.stream(financialStatement.getDate().split("-")).findFirst().get()
                  + " "
                  + jsonIncomeStatement.getJSONObject(i).getString("period"));
        } else {
          financialStatement =
              initMonthFinancialStatement(
                  jsonIncomeStatement.getJSONObject(i), jsonBalanceSheetStatement.getJSONObject(i));
        }
        list.add(financialStatement);
      }
    }
    return list;
  }

  private MonthFinancialStatementDto initMonthFinancialStatement(
      JSONObject jsonIncomeStatement, JSONObject jsonBalanceSheetStatement) {
    return new MonthFinancialStatementDto(
        jsonIncomeStatement.getBigDecimal("revenue"),
        jsonIncomeStatement.getBigDecimal("operatingIncome"),
        jsonIncomeStatement.getBigDecimal("netIncome"),
        jsonBalanceSheetStatement.getBigDecimal("cashAndCashEquivalents"),
        jsonBalanceSheetStatement.getBigDecimal("totalStockholdersEquity"));
  }

  private MonthFinancialStatementDto initMonthFinancialStatement(JSONObject jsonIncomeStatement) {
    return new MonthFinancialStatementDto(
        jsonIncomeStatement.getBigDecimal("revenue"),
        jsonIncomeStatement.getBigDecimal("operatingIncome"),
        jsonIncomeStatement.getBigDecimal("netIncome"),
        null,
        null);
  }

  private MonthFinancialStatementDto initMonthFinancialStatementWithDate(
      JSONObject jsonIncomeStatement, JSONObject jsonBalanceSheetStatement) {
    return new MonthFinancialStatementDto(
        jsonIncomeStatement.getBigDecimal("revenue"),
        jsonIncomeStatement.getBigDecimal("operatingIncome"),
        jsonIncomeStatement.getBigDecimal("netIncome"),
        jsonBalanceSheetStatement.getBigDecimal("cashAndCashEquivalents"),
        jsonBalanceSheetStatement.getBigDecimal("totalStockholdersEquity"),
        jsonBalanceSheetStatement.getString("date"));
  }

  private String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  public JSONArray readJsonFromUrl(String url) throws IOException, JSONException {
    try (InputStream is = new URL(url).openStream()) {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
      String jsonText = readAll(rd);
      if (jsonText.length() <= 10) {
        return null;
      }
      return new JSONArray(jsonText);
    }
  }
}
