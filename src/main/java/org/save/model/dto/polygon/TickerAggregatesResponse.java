package org.save.model.dto.polygon;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class TickerAggregatesResponse {
  private String ticker;
  private Long queryCount;
  private Long resultsCount;
  private Boolean adjusted;
  private List<Aggregate> results;
  private String status;

  @JsonProperty("request_id")
  private String requestId;

  private Long count;

  @Data
  public static class Aggregate {
    @JsonProperty("o")
    private BigDecimal open;

    @JsonProperty("c")
    private BigDecimal close;

    @JsonProperty("v")
    private BigDecimal volume;

    @JsonProperty("h")
    private BigDecimal high;

    @JsonProperty("l")
    private BigDecimal low;

    @JsonProperty("t")
    private Long date;
  }
}
