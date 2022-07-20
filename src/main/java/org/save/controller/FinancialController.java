package org.save.controller;

import lombok.RequiredArgsConstructor;
import org.save.model.dto.portfolio.PortfolioResponse;
import org.save.service.finance.OrderService;
import org.save.service.implementation.PortfolioService;
import org.save.service.implementation.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/finance")
@RequiredArgsConstructor
public class FinancialController {

  private final WalletService walletService;
  private final OrderService orderService;
  private final PortfolioService portfolioService;

  @Secured({"ROLE_USER", "ROLE_BLACKLIST", "ROLE_BANNED"})
  @GetMapping("/portfolio")
  public ResponseEntity<PortfolioResponse> getPortfolio(@RequestParam("userId") Long userId) {
    return new ResponseEntity<>(portfolioService.getPortfolio(userId), HttpStatus.OK);
  }

  @Secured({"ROLE_USER"})
  @GetMapping("/history/transactions")
  public ResponseEntity<?> getAccountTransactionsHistory(
      @RequestParam("accountId") String accountId) {
    return new ResponseEntity<>(walletService.getTransactionsInfo(accountId), HttpStatus.OK);
  }

  @Secured({"ROLE_USER"})
  @GetMapping("/history/trades")
  public ResponseEntity<?> getHistoricalTradesInfo(@RequestParam("accountId") String accountId) {
    return new ResponseEntity<>(walletService.getTradeHistory(accountId), HttpStatus.OK);
  }

  @Secured({"ROLE_USER"})
  @GetMapping("/history/all")
  public ResponseEntity<?> getAccountOperationsHistory(
      @RequestParam("accountId") String accountId) {
    return new ResponseEntity<>(walletService.getAllHistory(accountId), HttpStatus.OK);
  }
}
