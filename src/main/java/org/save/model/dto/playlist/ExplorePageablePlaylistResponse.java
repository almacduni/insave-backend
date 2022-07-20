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
public class ExplorePageablePlaylistResponse {
  private List<ExplorePlaylistResponse> playlists;
  private Integer currentPage;
  private Long totalCount;
  private Integer offset;
}
