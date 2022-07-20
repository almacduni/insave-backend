package org.save.client;

import com.mashape.unirest.http.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.save.model.dto.tatum.AccountBalanceDto;
import org.save.model.dto.tatum.ActiveTradesRequest;
import org.save.model.dto.tatum.AddressDto;
import org.save.model.dto.tatum.BlockchainEstimateRequest;
import org.save.model.dto.tatum.BlockchainEstimateResponse;
import org.save.model.dto.tatum.BlockedAmountsResponse;
import org.save.model.dto.tatum.BtcWithdrawRequest;
import org.save.model.dto.tatum.CreateCryptoTradeRequest;
import org.save.model.dto.tatum.CreateLedgerAccountRequest;
import org.save.model.dto.tatum.CreateSubscriptionRequest;
import org.save.model.dto.tatum.CreateTransactionsInfoRequest;
import org.save.model.dto.tatum.EthWithdrawRequest;
import org.save.model.dto.tatum.LedgerAccountDto;
import org.save.model.dto.tatum.PaymentReferenceResponse;
import org.save.model.dto.tatum.TatumResponse;
import org.save.model.dto.tatum.TradeHistoryDto;
import org.save.model.dto.tatum.TradeOrderResponse;
import org.save.model.dto.tatum.TransactionDto;
import org.save.model.dto.tatum.TransferRequest;
import org.save.model.dto.tatum.WithdrawResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TatumClient {

  @Value("${insave.integration.finances.tatum.api_key}")
  private final String API_KEY;

  @SneakyThrows
  public LedgerAccountDto getLedgerAccount(String accountId) {
    return Unirest.get("https://api-eu1.tatum.io/v3/ledger/account/{accountId}")
        .header("x-api-key", API_KEY)
        .routeParam(accountId, accountId)
        .asObject(LedgerAccountDto.class)
        .getBody();
  }

  @SneakyThrows
  public LedgerAccountDto createLedgerAccount(CreateLedgerAccountRequest ledgerAccountRequest) {
    return Unirest.post("https://api-eu1.tatum.io/v3/ledger/account")
        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .header("x-api-key", API_KEY)
        .body(ledgerAccountRequest)
        .asObject(LedgerAccountDto.class)
        .getBody();
  }

  @SneakyThrows
  public AddressDto createAddress(String accountId) {
    return Unirest.post("https://api-eu1.tatum.io/v3/offchain/account/{id}/address")
        .header("x-api-key", API_KEY)
        .routeParam("id", accountId)
        .asObject(AddressDto.class)
        .getBody();
  }

  @SneakyThrows
  public AddressDto assignAddressForAccount(String accountId, String address) {
    return Unirest.post("https://api-eu1.tatum.io/v3/offchain/account/{id}/address/{address}")
        .header("x-api-key", API_KEY)
        .routeParam("id", accountId)
        .routeParam("address", address)
        .asObject(AddressDto.class)
        .getBody();
  }

  @SneakyThrows
  public int createSubscription(CreateSubscriptionRequest createSubscriptionRequest) {
    return Unirest.post("https://api-eu1.tatum.io/v3/subscription")
        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .header("x-api-key", API_KEY)
        .body(createSubscriptionRequest)
        .asObject(Object.class)
        .getStatus();
  }

  @SneakyThrows
  public TransactionDto[] getTransactionsInfoByAccountId(
      CreateTransactionsInfoRequest createTransactionsInfoRequest) {
    return Unirest.post("https://api-eu1.tatum.io/v3/ledger/transaction/account?pageSize=50")
        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .header("x-api-key", API_KEY)
        .body(createTransactionsInfoRequest)
        .asObject(TransactionDto[].class)
        .getBody();
  }

  @SneakyThrows
  public TradeHistoryDto[] getTradeHistory(String accountId) {
    return Unirest.get("https://api-eu1.tatum.io/v3/trade/history?pageSize=50&id={accountId}")
        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .header("x-api-key", API_KEY)
        .routeParam("accountId", accountId)
        .asObject(TradeHistoryDto[].class)
        .getBody();
  }

  @SneakyThrows
  public TatumResponse createTradeOrder(CreateCryptoTradeRequest createTrade) {
    return Unirest.post("https://api-eu1.tatum.io/v3/trade")
        .header("content-type", "application/json")
        .header("x-api-key", API_KEY)
        .body(createTrade)
        .asObject(TatumResponse.class)
        .getBody();
  }

  @SneakyThrows
  public int cancelTradeOrder(String orderId) {
    return Unirest.delete("https://api-eu1.tatum.io/v3/trade/{id}")
        .header("x-api-key", API_KEY)
        .routeParam("id", orderId)
        .asObject(Object.class)
        .getStatus();
  }

  @SneakyThrows
  public int cancelAllTradeOrders(String accountId) {
    return Unirest.delete("https://api-eu1.tatum.io/v3/trade/account/{id}")
        .header("x-api-key", API_KEY)
        .routeParam("id", accountId)
        .asObject(Object.class)
        .getStatus();
  }

  @SneakyThrows
  public TradeOrderResponse getTradeOrder(String orderId) {
    return Unirest.get("https://api-eu1.tatum.io/v3/trade/{id}")
        .header("x-api-key", API_KEY)
        .routeParam("id", orderId)
        .asObject(TradeOrderResponse.class)
        .getBody();
  }

  @SneakyThrows
  public AccountBalanceDto getBalance(String accountId) {
    return Unirest.get("https://api-eu1.tatum.io/v3/ledger/account/{id}/balance")
        .header("x-api-key", API_KEY)
        .routeParam("id", accountId)
        .asObject(AccountBalanceDto.class)
        .getBody();
  }

  @SneakyThrows
  public LedgerAccountDto[] getCustomerAccounts(String innerUserId) {
    return Unirest.get(
            "https://api-eu1.tatum.io/v3/ledger/account/customer/{id}?pageSize=10&offset=0")
        .header("x-api-key", API_KEY)
        .routeParam("id", innerUserId)
        .asObject(LedgerAccountDto[].class)
        .getBody();
  }

  @SneakyThrows
  public WithdrawResponse withdrawBitcoin(BtcWithdrawRequest bitcoinWithdrawRequest) {
    return Unirest.post("https://api-eu1.tatum.io/v3/offchain/bitcoin/transfer")
        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .header("x-api-key", API_KEY)
        .body(bitcoinWithdrawRequest)
        .asObject(WithdrawResponse.class)
        .getBody();
  }

  @SneakyThrows
  public WithdrawResponse withdrawEthereum(EthWithdrawRequest ethereumWithdrawRequest) {
    return Unirest.post("https://api-eu1.tatum.io/v3/offchain/ethereum/transfer")
        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .header("x-api-key", API_KEY)
        .body(ethereumWithdrawRequest)
        .asObject(WithdrawResponse.class)
        .getBody();
  }

  @SneakyThrows
  public BlockchainEstimateResponse blockchainEstimate(BlockchainEstimateRequest request) {
    return Unirest.post("https://api-eu1.tatum.io/v3/offchain/blockchain/estimate")
        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .header("x-api-key", API_KEY)
        .body(request)
        .asObject(BlockchainEstimateResponse.class)
        .getBody();
  }

  @SneakyThrows
  public PaymentReferenceResponse makeTransfer(TransferRequest request) {
    return Unirest.post("https://api-eu1.tatum.io/v3/ledger/transaction")
        .header("content-type", "application/json")
        .header("x-api-key", API_KEY)
        .body(request)
        .asObject(PaymentReferenceResponse.class)
        .getBody();
  }

  @SneakyThrows
  public TradeHistoryDto[] getAllActiveTradesByCustomerIdList(ActiveTradesRequest request) {
    return Unirest.get(
            "https://api-eu1.tatum.io/v3/trade/{type}?customerId={customerId}&pageSize={pageSize}&offset={offset}")
        .header("content-type", "application/json")
        .header("x-api-key", API_KEY)
        .routeParam("type", request.getOrderType())
        .routeParam("customerId", request.getCustomerId())
        .routeParam("pageSize", String.valueOf(request.getPageSize()))
        .routeParam("offset", String.valueOf(request.getOffset()))
        .asObject(TradeHistoryDto[].class)
        .getBody();
  }

  @SneakyThrows
  public TradeHistoryDto[] getAllActiveTradesList(ActiveTradesRequest request) {
    return Unirest.get(
            "https://api-eu1.tatum.io/v3/trade/{type}?pageSize={pageSize}&offset={offset}&pair={pair}")
        .header("content-type", "application/json")
        .header("x-api-key", API_KEY)
        .routeParam("type", request.getOrderType())
        .routeParam("pageSize", String.valueOf(request.getPageSize()))
        .routeParam("offset", String.valueOf(request.getOffset()))
        .routeParam("pair", request.getPair())
        .asObject(TradeHistoryDto[].class)
        .getBody();
  }

  @SneakyThrows
  public BlockedAmountsResponse[] getBlockedAmountsInAccount(
      String accountId, Integer pageSize, Integer offset) {
    return Unirest.get(
            "https://api-eu1.tatum.io/v3/ledger/account/block/{accountId}?pageSize={pageSize}&offset={offset}")
        .header("content-type", "application/json")
        .header("x-api-key", API_KEY)
        .routeParam("accountId", accountId)
        .routeParam("pageSize", String.valueOf(pageSize))
        .routeParam("offset", String.valueOf(offset))
        .asObject(BlockedAmountsResponse[].class)
        .getBody();
  }
}
