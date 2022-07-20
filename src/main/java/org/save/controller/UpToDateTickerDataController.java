package org.save.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.save.model.dto.ticker.UpToDateTickerResponse;
import org.save.service.UpToDateTickerService;
import org.save.service.playlist.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UpToDateTickerDataController {

  private final UpToDateTickerService upToDateTickerService;
  private final StockService stockService;

  @Secured({"ROLE_ADMIN"})
  @GetMapping("/up-to-date/get-info")
  public ResponseEntity<UpToDateTickerResponse> upToDate(@RequestParam("ticker") String ticker) {
    return upToDateTickerService
        .getUpToDateTicker(ticker)
        .map(ResponseEntity::ok)
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @Secured({"ROLE_ADMIN"})
  @PutMapping("/up-to-date/updateTickers")
  public ResponseEntity<Object> updateTickers() {
    log.info("PUT /up-to-date/updateTickers");

    stockService.fetchAndSaveTickers();

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Secured({"ROLE_ADMIN"})
  @Scheduled(cron = "@weekly") // execute every week in 00:00
  @PutMapping("/up-to-date/amg")
  public ResponseEntity<?> updateAmg() {
    stockService.updateAllAmgByTickerName();
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
