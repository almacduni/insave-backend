package org.save.model.dto.playlist;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistResponse {

  private Long id;
  private String username;
  private String title;
  private String description;
  private String imageURL;
  private List<TickerResponse> tickers;
  // TODO field is not necessary
  // private List<PlaylistCategoryResponse> categories;

}
