package org.save.model.dto.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.save.model.dto.polygon.CloseStockDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralInfoInSearchDto {

  @JsonProperty("symbol")
  private String ticker;

  private String description;

  @JsonProperty("image")
  private String logo;

  @JsonProperty("name")
  private String companyName;

  @JsonProperty("changesPercentage")
  private BigDecimal changesPercentage;

  @JsonProperty("price")
  private BigDecimal price;

  @JsonProperty("change")
  private BigDecimal change;

  @JsonProperty("eps")
  private BigDecimal EPS;

  @JsonProperty("historical")
  private List<CloseStockDto> historical = new ArrayList<>();
}
