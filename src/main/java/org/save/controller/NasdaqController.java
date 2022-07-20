package org.save.controller;

import lombok.RequiredArgsConstructor;
import org.save.model.dto.nasdaq.QuarterlyEarningsResponse;
import org.save.service.implementation.NasdaqService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/nasdaq")
@RequiredArgsConstructor
@Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
public class NasdaqController {

  private final NasdaqService nasdaqService;

  @GetMapping("/getByTicker")
  public ResponseEntity<QuarterlyEarningsResponse> getNasdaqQuarterlyEarnings(
      @RequestParam("ticker") String ticker) {
    if (!ticker.isEmpty()) {
      try {
        QuarterlyEarningsResponse quarterlyEarningsResponse =
            nasdaqService.getQuarterlyEarnings(ticker);

        if (quarterlyEarningsResponse != null) {
          return new ResponseEntity<>(quarterlyEarningsResponse, HttpStatus.OK);
        } else {
          return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
      } catch (Exception exception) {
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
  }
}
