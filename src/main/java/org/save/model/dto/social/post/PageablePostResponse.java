package org.save.model.dto.social.post;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageablePostResponse {

  private List<PostResponse> posts;
  private Integer currentPage;
  private Long totalCount;
  private Integer offset;
}
