package org.save.service.watchlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.save.client.FinancialModellingClient;
import org.save.model.dto.watchlist.WatchlistItem;
import org.save.model.dto.watchlist.WatchlistResponse;
import org.save.model.entity.social.playlist.TickerStatus;
import org.save.model.entity.watchlist.Watchlist;
import org.save.service.implementation.TickerStatusService;
import org.springframework.stereotype.Service;

/** Service responsible for managing {@link WatchlistResponse} and {@link WatchlistItem} */
@Slf4j
@Service
@RequiredArgsConstructor
public class WatchListResponseService {

  private static final String IMAGE_NOT_FOUND = "Image not found";

  private final FinancialModellingClient financialModellingClient;
  private final TickerStatusService tickerStatusService;
  private final WatchlistService watchlistService;

  /**
   * Creates and returns default watch list with default tickers
   *
   * @return created watch list response
   */
  public WatchlistResponse getDefaultWatchListResponse() {
    return convertToWatchlistResponse(watchlistService.getDefaultWatchList());
  }

  /**
   * Gets user watch list by id
   *
   * @param userId user id (shouldn't be null)
   * @return found watch list response
   */
  public WatchlistResponse getUserWatchlistResponse(Long userId) {
    Watchlist watchlist = watchlistService.findWatchlistByUserId(userId);
    return convertToWatchlistResponse(watchlist);
  }

  /**
   * Gets watch list item by provided ticker
   *
   * @param ticker ticker to get watch list item
   * @return created watch list item
   */
  public WatchlistItem getWatchlistItem(String ticker) {
    WatchlistItem watchlistItem = financialModellingClient.getWatchListItem(ticker);
    watchlistItem.setLogo(
        getLogo(financialModellingClient.getCompanyAdditionalInfo(ticker.toUpperCase())));
    TickerStatus tickerStatus = tickerStatusService.getTickerStatusByTicker(ticker);
    watchlistItem.setTradingStatus(tickerStatus.getTradingStatus());
    watchlistItem.setMarketDataSupported(tickerStatus.isMarketDataSupported());
    return watchlistItem;
  }

  private WatchlistResponse convertToWatchlistResponse(Watchlist watchlist) {
    List<WatchlistItem> watchlistItemList = convertTickersToWatchListItems(watchlist.getTickers());
    return new WatchlistResponse(watchlist.getId(), watchlistItemList);
  }

  private String getLogo(WatchlistItem watchlistItem) {
    String logo = watchlistItem.getLogo();
    return logo == null ? IMAGE_NOT_FOUND : logo;
  }

  private List<WatchlistItem> convertTickersToWatchListItems(List<String> tickers) {
    int countOfTickersInWatchList = tickers.size();
    if (countOfTickersInWatchList == 0) {
      return new ArrayList<>();
    }

    ExecutorService executorService = Executors.newFixedThreadPool(countOfTickersInWatchList);
    Map<Integer, Future<WatchlistItem>> futureMap = new HashMap<>(countOfTickersInWatchList);
    List<WatchlistItem> watchlistItemList = new ArrayList<>(countOfTickersInWatchList);

    for (int index = 0; index < countOfTickersInWatchList; index++) {
      final String ticker = tickers.get(index);
      futureMap.put(index, executorService.submit(() -> getWatchlistItem(ticker)));
    }

    futureMap.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .forEach(futureMapEntry -> putValueIntoList(watchlistItemList, futureMapEntry));

    executorService.shutdown();
    return watchlistItemList;
  }

  private void putValueIntoList(
      List<WatchlistItem> watchlistItemList,
      Map.Entry<Integer, Future<WatchlistItem>> futureMapEntry) {
    try {
      watchlistItemList.add(futureMapEntry.getValue().get());
    } catch (InterruptedException | ExecutionException exception) {
      log.error("Async watchlist ticker fetching error:", exception);
    }
  }
}
