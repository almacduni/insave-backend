package org.save.model.dto.playlist;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrUpdatePlaylistRequest {

  private String title;
  private String description;
  private List<Long> tickers;
}
