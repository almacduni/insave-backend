package org.save.service.watchlist;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.save.exception.InvalidTickerException;
import org.save.exception.NoSuchObjectException;
import org.save.model.entity.common.User;
import org.save.model.entity.watchlist.Watchlist;
import org.save.repo.WatchlistRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Service responsible for managing {@link Watchlist} entity */
@Slf4j
@Service
@RequiredArgsConstructor
public class WatchlistService {

  private static final String WATCHLIST_LIMIT_IS_EXCEEDED = "Watchlist limit is exceeded";
  private static final String WATCHLIST_NOT_FOUND = "Watchlist not found";

  private final WatchlistRepository watchlistRepository;

  @Value("#{'${insave.integration.finances.watchlist.default.tickers}'.split(',')}")
  private List<String> defaultTickers;

  @Value("${insave.integration.finances.watchlist.ticker.limit}")
  private Integer watchListTickerLimit;

  /**
   * Creates and saves {@link Watchlist} for the provided {@link User}
   *
   * @param user user
   * @return saved watchlist
   */
  public Watchlist createWatchlistForUser(User user) {
    Watchlist watchlist = new Watchlist();
    watchlist.setUser(user);
    watchlist.setTickers(defaultTickers);
    return watchlistRepository.save(watchlist);
  }

  /**
   * Creates and returns default watch list with default tickers
   *
   * @return created watch list
   */
  public Watchlist getDefaultWatchList() {
    return Watchlist.builder().tickers(defaultTickers).build();
  }

  /**
   * Creates and returns default watch list with default tickers
   *
   * @return created watch list
   */
  public Watchlist findWatchlistByUserId(Long userId) {
    return watchlistRepository
        .findWatchlistByUserId(userId)
        .orElseThrow(() -> new NoSuchObjectException(WATCHLIST_NOT_FOUND));
  }

  /**
   * Updates watch list with new tickers
   *
   * @param watchlistId - watch list id to update
   * @param tickers - new list of tickers
   */
  public void updateWatchlist(Long watchlistId, List<String> tickers) {
    Watchlist watchlist =
        watchlistRepository
            .findById(watchlistId)
            .orElseThrow(() -> new NoSuchObjectException(WATCHLIST_NOT_FOUND));

    if (watchlist.getTickers().size() > watchListTickerLimit) {
      throw new InvalidTickerException(WATCHLIST_LIMIT_IS_EXCEEDED);
    } else {
      watchlist.setTickers(tickers);
      watchlistRepository.save(watchlist);
    }
  }
}
