package org.save.service.serach;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.save.model.dto.search.SearchTickerResponse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageableSearchResponse {

  private List<SearchTickerResponse> searchTickerResponseList;
  private Integer currentPage;
  private Long totalCount;
  private Integer offset;
}
