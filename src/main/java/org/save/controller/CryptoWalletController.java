package org.save.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.save.model.dto.financialmodelling.TransferRequest;
import org.save.model.dto.polygon.PrevClose;
import org.save.model.dto.tatum.AccountBalanceDto;
import org.save.model.dto.tatum.BalanceResponse;
import org.save.model.dto.tatum.BlockedAmountsResponse;
import org.save.model.dto.tatum.CryptoTradeRequest;
import org.save.model.dto.tatum.CustomerTradeMatchRequest;
import org.save.model.dto.tatum.IncomingTransactionRequest;
import org.save.model.dto.tatum.PaymentReferenceResponse;
import org.save.model.dto.tatum.TradeHistoryDto;
import org.save.model.dto.tatum.WithdrawRequest;
import org.save.model.dto.tatum.WithdrawResponse;
import org.save.model.enums.CryptoCurrency;
import org.save.service.finance.OrderService;
import org.save.service.implementation.CryptoWalletService;
import org.save.service.implementation.WalletService;
import org.save.util.PolygonAPI;
import org.save.util.cryptocurrency.BitcoinsService;
import org.save.util.cryptocurrency.CryptoService;
import org.save.util.cryptocurrency.EthereumService;
import org.save.util.cryptocurrency.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crypto")
@RequiredArgsConstructor
public class CryptoWalletController {

  private final CryptoWalletService cryptoCurrencyService;
  private final EthereumService ethereumService;
  private final BitcoinsService bitcoinsService;
  private final TokenService tokenService;
  private final CryptoService cryptoService;
  private final OrderService orderService;
  private final WalletService walletService;
  private final PolygonAPI polygonAPI;

  @GetMapping("/btcAddress")
  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  public ResponseEntity<String> getBtcAddress(Long userId) {
    return new ResponseEntity<>(bitcoinsService.getAddress(userId), HttpStatus.OK);
  }

  @PostMapping("/transaction")
  public ResponseEntity<?> receiveIncomingTransaction(
      @RequestBody IncomingTransactionRequest incomingTransaction) {
    cryptoService.foundAccountWithCurrency(incomingTransaction);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/order")
  public ResponseEntity<?> receiveClosedTrade(
      @RequestBody CustomerTradeMatchRequest customerTradeMatch) {
    cryptoService.foundAccountsWithOrder(customerTradeMatch);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @GetMapping("/ethAddress")
  public ResponseEntity<String> getEthAddress(Long userId) {
    return new ResponseEntity<>(ethereumService.getAddress(userId), HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @GetMapping("/tokenAddress")
  public ResponseEntity<String> getTokenAddress(Long userId) {
    return new ResponseEntity<>(tokenService.getAddress(userId), HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @PostMapping("/eth/withdraw")
  public ResponseEntity<WithdrawResponse> withdrawEthereum(
      @RequestBody WithdrawRequest request, @RequestParam int code) {
    return new ResponseEntity<>(cryptoCurrencyService.withdrawEth(request, code), HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @PostMapping("/btc/withdraw")
  public ResponseEntity<WithdrawResponse> withdrawBitcoin(
      @RequestBody WithdrawRequest request, @RequestParam int code) {
    return new ResponseEntity<>(cryptoCurrencyService.withdrawBtc(request, code), HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @PostMapping("/securityCode")
  public ResponseEntity<?> sendSecurityCode(
      @RequestBody WithdrawRequest request, @RequestParam CryptoCurrency currency) {
    cryptoCurrencyService.sendSecurityCode(request, currency);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED"})
  @PostMapping("/orders")
  public ResponseEntity<String> createCryptoOrder(@RequestBody CryptoTradeRequest request) {
    String id =
        orderService.createCryptoOrder(
            request.getUserId(),
            request.getOrderType(),
            request.getPrice(),
            request.getAmount(),
            request.getFirstCurrency(),
            request.getSecondCurrency(),
            request.getIsPriceSet());

    return new ResponseEntity<>(id, HttpStatus.CREATED);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED"})
  @DeleteMapping("/orders/{orderId}")
  public ResponseEntity<?> cancelCryptoOrder(@PathVariable String orderId) {
    orderService.cancelCryptoOrder(orderId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED"})
  @DeleteMapping("/orders")
  public ResponseEntity<?> cancelAllCryptoOrders(
      @RequestParam Long userId, @RequestParam CryptoCurrency currency) {
    orderService.cancelAllCryptoOrders(userId, currency);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Secured({"ROLE_USER"})
  @GetMapping("/blockedAmounts")
  public ResponseEntity<?> getBlockedAmountsInAccount(
      @RequestParam Long userId,
      @RequestParam CryptoCurrency currency,
      @RequestParam Integer pageSize,
      @RequestParam Integer offset) {
    List<BlockedAmountsResponse> blockedAmounts =
        orderService.getBlockedAmountsInAccount(userId, currency, pageSize, offset);

    return new ResponseEntity<>(blockedAmounts, HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED"})
  @GetMapping("/activeTrades")
  public ResponseEntity<List<TradeHistoryDto>> getListActiveTrades(
      @RequestParam Long userId,
      @RequestParam String orderType,
      @RequestParam Integer pageSize,
      @RequestParam Integer offset) {
    List<TradeHistoryDto> activeTrades =
        orderService.getListActiveTrades(userId, orderType, pageSize, offset);

    return new ResponseEntity<>(activeTrades, HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @GetMapping("/balance")
  public ResponseEntity<AccountBalanceDto> getBalance(Long userId, CryptoCurrency cryptoCurrency) {
    AccountBalanceDto response =
        walletService.getCryptoBalanceFromTatumByUserId(userId, cryptoCurrency);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @GetMapping("/allBalance")
  public ResponseEntity<List<BalanceResponse>> getAllBalance(Long userId) {
    List<BalanceResponse> response = walletService.getCryptoBalanceFromTatumForUser(userId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED", "ROLE_BLACKLIST"})
  @GetMapping("/prev/close/{ticker}")
  public ResponseEntity<PrevClose> getLastTradeOfCryptoPair(@PathVariable String ticker) {
    return new ResponseEntity<>(polygonAPI.getPrevClose(ticker), HttpStatus.OK);
  }

  @Secured({"ROLE_USER"})
  @PostMapping("/transfer")
  public ResponseEntity<PaymentReferenceResponse> makeTransfer(
      @RequestBody TransferRequest transferRequest) {
    return new ResponseEntity<>(walletService.makeTransfer(transferRequest), HttpStatus.OK);
  }
}
