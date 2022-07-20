package org.save.model.dto.playlist;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoriesResponse {

  private List<PlaylistCategoryResponse> categories;
  private Integer currentPage;
  private Long totalCount;
  private Integer offset;
}
