package org.save.service.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.save.client.TatumClient;
import org.save.exception.NoSuchObjectException;
import org.save.exception.TatumCancelTradeException;
import org.save.exception.TatumTradeException;
import org.save.model.dto.tatum.ActiveTradesRequest;
import org.save.model.dto.tatum.BlockedAmountsResponse;
import org.save.model.dto.tatum.CreateCryptoTradeRequest;
import org.save.model.dto.tatum.TatumResponse;
import org.save.model.dto.tatum.TradeHistoryDto;
import org.save.model.entity.common.Wallet;
import org.save.model.entity.tatum.WalletAccount;
import org.save.model.enums.CryptoCurrency;
import org.save.model.enums.OrderType;
import org.save.repo.WalletRepository;
import org.save.service.implementation.WalletService;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class OrderService {

  private static final Integer TATUM_MAX_PAGE_SIZE = 50;

  private final WalletRepository walletRepository;
  private final WalletService walletService;
  private final TatumClient tatumClient;

  public String createCryptoOrder(
      Long userId,
      OrderType orderType,
      String price,
      BigDecimal amount,
      CryptoCurrency firstCurrency,
      CryptoCurrency secondCurrency,
      Boolean isPriceSet) {
    String pair = firstCurrency.name() + "/" + secondCurrency.name();

    WalletAccount firstAccount = walletService.getWalletAccount(userId, firstCurrency);
    WalletAccount secondAccount = walletService.getWalletAccount(userId, secondCurrency);

    String currency1AccountId = firstAccount.getLedgerAccountId();
    String currency2AccountId = secondAccount.getLedgerAccountId();

    if (!isPriceSet) {
      ActiveTradesRequest activeTradesRequest = prepareDataForRequest(userId, orderType, pair);
      String foundPrice = findCryptoOrderPrice(activeTradesRequest);

      if (foundPrice != null) {
        price = foundPrice;
      }
    }

    CreateCryptoTradeRequest request =
        CreateCryptoTradeRequest.builder()
            .type(orderType)
            .price(String.valueOf(price))
            .amount(String.valueOf(amount))
            .pair(pair)
            .currency1AccountId(currency1AccountId)
            .currency2AccountId(currency2AccountId)
            .build();

    return createCryptoOrderWithSetPrice(request);
  }

  private ActiveTradesRequest prepareDataForRequest(Long userId, OrderType orderType, String pair) {
    Wallet userWallet = getWalletByUserId(userId);
    OrderType oppositeOrderType = (orderType == OrderType.BUY) ? OrderType.SELL : OrderType.BUY;

    ActiveTradesRequest activeTradesRequest =
        ActiveTradesRequest.builder()
            .customerId(userWallet.getCustomerId())
            .orderType(oppositeOrderType.name())
            .pair(pair)
            .pageSize(TATUM_MAX_PAGE_SIZE)
            .offset(0)
            .build();

    return activeTradesRequest;
  }

  private String createCryptoOrderWithSetPrice(CreateCryptoTradeRequest request) {
    TatumResponse tatumResponse = tatumClient.createTradeOrder(request);

    if (tatumResponse.getId() == null) {
      throw new TatumTradeException(tatumResponse.getMessage());
    }

    log.info("An order was created with ID = {}", tatumResponse.getId());
    return tatumResponse.getId();
  }

  private String findCryptoOrderPrice(ActiveTradesRequest request) {
    String foundPrice;

    List<TradeHistoryDto> activeTrades = Arrays.asList(tatumClient.getAllActiveTradesList(request));
    List<TradeHistoryDto> allActiveTrades = new ArrayList<>(activeTrades);
    allActiveTrades.addAll(activeTrades);

    while (activeTrades.size() == TATUM_MAX_PAGE_SIZE) {
      request.setOffset(request.getOffset() + TATUM_MAX_PAGE_SIZE);
      activeTrades = Arrays.asList(tatumClient.getAllActiveTradesList(request));
      allActiveTrades.addAll(activeTrades);
    }

    if (request.getOrderType().equals(OrderType.BUY.name())) {
      foundPrice =
          allActiveTrades.stream()
              .max(
                  (TradeHistoryDto el1, TradeHistoryDto el2) ->
                      (int)
                          (Double.parseDouble(el1.getPrice()) - Double.parseDouble(el2.getPrice())))
              .map(TradeHistoryDto::getPrice)
              .orElse(null);
    } else {
      foundPrice =
          allActiveTrades.stream()
              .min(
                  (TradeHistoryDto el1, TradeHistoryDto el2) ->
                      (int)
                          (Double.parseDouble(el1.getPrice()) - Double.parseDouble(el2.getPrice())))
              .map(TradeHistoryDto::getPrice)
              .orElse(null);
    }
    return foundPrice;
  }

  public void cancelCryptoOrder(String orderId) {
    int status = tatumClient.cancelTradeOrder(orderId);
    if (status != 204) {
      throw new TatumCancelTradeException("status code: " + status);
    }
    log.info("Order with id = {} was canceled with status = {}", orderId, status);
  }

  public void cancelAllCryptoOrders(Long userId, CryptoCurrency currency) {
    String accountId =
        walletRepository
            .findLegerAccountByUserIdAndCryptoCurrency(userId, currency.ordinal())
            .orElseThrow(
                () ->
                    new NoSuchObjectException(
                        "LedgerAccount with CryptoCurrency = "
                            + " for a user with id = "
                            + userId
                            + " not found"));

    int status = tatumClient.cancelAllTradeOrders(accountId);
    if (status != 204) {
      throw new TatumCancelTradeException("status code: " + status);
    }

    log.info(
        "All orders for an account with id = {} was canceled with status = {}", accountId, status);
  }

  public List<TradeHistoryDto> getListActiveTrades(
      Long userId, String orderType, Integer pageSize, Integer offset) {
    Wallet wallet = getWalletByUserId(userId);

    ActiveTradesRequest request =
        ActiveTradesRequest.builder()
            .orderType(orderType)
            .customerId(wallet.getCustomerId())
            .pageSize(pageSize)
            .offset(offset)
            .build();

    return Arrays.asList(tatumClient.getAllActiveTradesByCustomerIdList(request));
  }

  private Wallet getWalletByUserId(Long userId) {
    return walletRepository.getWalletByUserId(userId);
  }

  public List<BlockedAmountsResponse> getBlockedAmountsInAccount(
      Long userId, CryptoCurrency currency, Integer pageSize, Integer offset) {
    String accountId =
        walletRepository
            .findLegerAccountByUserIdAndCryptoCurrency(userId, currency.ordinal())
            .orElseThrow(
                () ->
                    new NoSuchObjectException(
                        "LedgerAccount with CryptoCurrency = "
                            + " for a user with id = "
                            + userId
                            + " not found"));

    return Arrays.asList(tatumClient.getBlockedAmountsInAccount(accountId, pageSize, offset));
  }
}
