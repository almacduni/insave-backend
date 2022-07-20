package org.save.model.dto.financialmodelling;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.save.model.enums.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDescription {

  private final Currency currency = Currency.USD;

  @JsonProperty("volAvg")
  private BigDecimal volAVG;

  private Double insiderOwn;

  private Double instOwn;

  private Double targetPrice;

  @JsonProperty("peRatioTTM")
  private Double peRationTTM;

  @JsonProperty("dividendYieldTTM")
  private Double dividendYielPercentageTTM;

  @JsonProperty("returnOnAssetsTTM")
  private Double returnOnAssetsTTM;

  @JsonProperty("returnOnEquityTTM")
  private Double returnOnEquityTTM;

  @JsonProperty("marketCap")
  private BigDecimal mktCap;

  private Double recom;

  private BigDecimal amg;

  private BigDecimal currentRatio;
  private BigDecimal leverageRatio;

  private String nextEarningsDate;
  private List<FmpEarningsCalendar> eps;

  public CompanyDescription(Double insiderOwn, Double instOwn, Double targetPrice, Double recom) {
    this.insiderOwn = insiderOwn;
    this.instOwn = instOwn;
    this.targetPrice = targetPrice;
    this.recom = recom;
  }
}
