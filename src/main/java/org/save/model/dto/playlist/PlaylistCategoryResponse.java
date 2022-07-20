package org.save.model.dto.playlist;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistCategoryResponse {

  private Long id;
  private String category;
  private Integer serialNumber;
  private List<ExplorePlaylistResponse> playlists;

  public PlaylistCategoryResponse(Long id, String category, Integer serialNumber) {
    this.id = id;
    this.category = category;
    this.serialNumber = serialNumber;
  }
}
