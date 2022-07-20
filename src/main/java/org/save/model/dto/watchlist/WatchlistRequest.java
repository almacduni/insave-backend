package org.save.model.dto.watchlist;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchlistRequest {

  @NotNull private Long watchlistId;

  @NotNull private List<String> tickers;
}
