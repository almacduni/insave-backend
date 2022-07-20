package org.save.controller;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.save.model.dto.ticker.TickerStatusRequest;
import org.save.model.entity.social.playlist.TickerStatus;
import org.save.service.implementation.TickerStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickers")
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
public class TickerController {

  private final TickerStatusService tickerStatusService;

  @GetMapping("/statuses")
  public ResponseEntity<List<TickerStatus>> getTickersStatuses() {
    return new ResponseEntity<>(tickerStatusService.findAll(), HttpStatus.OK);
  }

  @GetMapping("/statuses/{ticker}")
  public ResponseEntity<TickerStatus> getTickerStatus(@PathVariable String ticker) {
    return new ResponseEntity<>(
        tickerStatusService.getTickerStatusByTicker(ticker.toUpperCase()), HttpStatus.OK);
  }

  @PostMapping("/statuses")
  public ResponseEntity<TickerStatus> createOrUpdateStatusForTicker(
      @RequestBody @Valid TickerStatusRequest tickerStatusRequest) {
    return new ResponseEntity<>(
        tickerStatusService.saveOrUpdateStatus(tickerStatusRequest), HttpStatus.OK);
  }

  @DeleteMapping("/statuses/{ticker}")
  @ResponseStatus(HttpStatus.OK)
  public void deleteStatus(@PathVariable String ticker) {
    tickerStatusService.deleteByTicker(ticker.toUpperCase());
  }
}
