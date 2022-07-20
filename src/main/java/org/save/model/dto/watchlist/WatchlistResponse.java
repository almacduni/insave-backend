package org.save.model.dto.watchlist;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchlistResponse {
  private Long watchlistId;
  private List<WatchlistItem> tickers;
}
