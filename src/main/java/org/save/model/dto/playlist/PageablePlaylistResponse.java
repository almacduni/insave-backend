package org.save.model.dto.playlist;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageablePlaylistResponse {

  private List<PlaylistResponse> playlists;
  private Integer currentPage;
  private Long totalCount;
  private Integer offset;
}
