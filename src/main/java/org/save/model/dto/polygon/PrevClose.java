package org.save.model.dto.polygon;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrevClose {

  private List<Result> results;

  @Data
  public static class Result {
    @JsonProperty("c")
    private BigDecimal close;
  }
}
