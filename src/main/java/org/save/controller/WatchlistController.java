package org.save.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.save.model.dto.watchlist.WatchlistItem;
import org.save.model.dto.watchlist.WatchlistRequest;
import org.save.model.dto.watchlist.WatchlistResponse;
import org.save.service.watchlist.WatchListResponseService;
import org.save.service.watchlist.WatchlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WatchlistController {

  private final WatchListResponseService watchListResponseService;
  private final WatchlistService watchlistService;

  @GetMapping("/watchlist/getUserWatchlist")
  public ResponseEntity<WatchlistResponse> getUserWatchlist(
      @RequestParam(name = "userId", required = false) Long userId) {
    log.info("GET: /watchlist/getUserWatchlist userId={}", userId);
    if (userId == null) {
      return new ResponseEntity<>(
          watchListResponseService.getDefaultWatchListResponse(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(
          watchListResponseService.getUserWatchlistResponse(userId), HttpStatus.OK);
    }
  }

  @Secured({"ROLE_USER", "ROLE_BANNED"})
  @PutMapping("/watchlist/update")
  public ResponseEntity<Object> updateWatchlist(
      @RequestBody @Valid WatchlistRequest watchlistRequest) {
    log.info("PUT: /watchlist/update watchlistRequest={}", watchlistRequest);
    watchlistService.updateWatchlist(
        watchlistRequest.getWatchlistId(), watchlistRequest.getTickers());
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED"})
  @GetMapping("/watchlist/company")
  public ResponseEntity<WatchlistItem> getWatchlistCompany(@RequestParam String ticker) {
    return new ResponseEntity<>(watchListResponseService.getWatchlistItem(ticker), HttpStatus.OK);
  }
}
