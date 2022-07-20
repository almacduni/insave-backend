package org.save.model.dto.search;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CompanySearchRequestDto extends CommonSearchRequestDto {

  private String ticker;
  private String companyName;
}
