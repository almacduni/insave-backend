package org.save.service.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.save.client.TatumClient;
import org.save.exception.InsufficientBalanceException;
import org.save.exception.NoSuchObjectException;
import org.save.model.dto.financialmodelling.TransferRequest;
import org.save.model.dto.tatum.AccountBalanceDto;
import org.save.model.dto.tatum.AccountOperation;
import org.save.model.dto.tatum.BalanceResponse;
import org.save.model.dto.tatum.CreateTransactionsInfoRequest;
import org.save.model.dto.tatum.PaymentReferenceResponse;
import org.save.model.dto.tatum.TradeHistoryDto;
import org.save.model.dto.tatum.TransactionDto;
import org.save.model.entity.common.User;
import org.save.model.entity.common.Wallet;
import org.save.model.entity.tatum.WalletAccount;
import org.save.model.enums.CryptoCurrency;
import org.save.model.enums.Currency;
import org.save.model.enums.TransactionType;
import org.save.repo.WalletRepository;
import org.save.util.mapper.tatum.TradesHistoryMapper;
import org.save.util.mapper.tatum.TransactionHistoryMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class WalletService {

  private final WalletRepository walletRepository;
  private final TatumClient tatumClient;
  private final TransactionHistoryMapper transactionHistoryMapper;
  private final TradesHistoryMapper tradesHistoryMapper;

  public Wallet createWallet(User user) {
    Wallet wallet = new Wallet(BigDecimal.ZERO, Currency.USD);
    wallet.setUser(user);
    return walletRepository.save(wallet);
  }

  public AccountBalanceDto getCryptoBalanceFromTatumByUserId(
      Long userId, CryptoCurrency cryptoCurrency) {
    WalletAccount walletAccount = getWalletAccount(userId, cryptoCurrency);
    if (walletAccount == null) {
      log.error("User with id={} hasn't wallet account for {}", userId, cryptoCurrency.name());
      return null;
    }
    return tatumClient.getBalance(walletAccount.getLedgerAccountId());
  }

  public WalletAccount getWalletAccount(Long userId, CryptoCurrency cryptoCurrency) {
    Wallet wallet = walletRepository.getWalletByUserId(userId);
    List<WalletAccount> accounts = wallet.getAccounts();
    for (WalletAccount account : accounts) {
      if (account.getCryptoCurrency().name().equals(cryptoCurrency.name())) {
        return account;
      }
    }
    throw new NoSuchObjectException("Crypto wallet for " + cryptoCurrency.name() + " not found");
  }

  public List<BalanceResponse> getCryptoBalanceFromTatumForUser(Long userId) {
    Wallet wallet = walletRepository.getWalletByUserId(userId);
    String customerId = wallet.getCustomerId();
    return Arrays.stream(tatumClient.getCustomerAccounts(customerId))
        .map(
            account -> {
              BalanceResponse balanceResponse = new BalanceResponse();
              balanceResponse.setBalance(account.getBalance());
              balanceResponse.setCurrency(account.getCurrency());
              balanceResponse.setActive(account.getActive());
              balanceResponse.setFrozen(account.getFrozen());
              balanceResponse.setCustomerId(account.getCustomerId());
              balanceResponse.setId(account.getId());
              balanceResponse.setXpub(account.getXpub());
              for (WalletAccount wa : wallet.getAccounts()) {
                if (wa.getLedgerAccountId().equals(account.getId())) {
                  balanceResponse.setAddress(wa.getAddress());
                  break;
                }
              }
              return balanceResponse;
            })
        .collect(Collectors.toList());
  }

  public List<TransactionDto> getTransactionsInfo(String accountId) {
    CreateTransactionsInfoRequest request = new CreateTransactionsInfoRequest(accountId);
    List<TransactionDto> transactionsList =
        Arrays.asList(tatumClient.getTransactionsInfoByAccountId(request));
    return transactionsList;
  }

  public List<TradeHistoryDto> getTradeHistory(String accountId) {
    List<TradeHistoryDto> tradesList = Arrays.asList(tatumClient.getTradeHistory(accountId));
    return tradesList;
  }

  public List<AccountOperation> getAllHistory(String accountId) {
    List<TransactionDto> transactionsHistory = getTransactionsInfo(accountId);
    List<TradeHistoryDto> tradesHistory = getTradeHistory(accountId);

    List<AccountOperation> allOperationsHistory = new ArrayList<>();

    List<AccountOperation> transactions =
        transactionsHistory.stream()
            .map(transactionHistoryMapper::convertToAccountOperation)
            .collect(Collectors.toList());

    List<AccountOperation> trades =
        tradesHistory.stream()
            .map(tradesHistoryMapper::convertToTradeOperation)
            .collect(Collectors.toList());

    allOperationsHistory.addAll(transactions);
    allOperationsHistory.addAll(trades);

    Collections.sort(allOperationsHistory, Collections.reverseOrder());

    return allOperationsHistory;
  }

  public PaymentReferenceResponse makeTransfer(TransferRequest transferRequest) {
    WalletAccount senderAccount =
        getWalletAccount(transferRequest.getSenderId(), transferRequest.getCryptoCurrency());
    WalletAccount recipientAccount =
        getWalletAccount(transferRequest.getRecipientId(), transferRequest.getCryptoCurrency());

    String senderAccountId = senderAccount.getLedgerAccountId();
    org.save.model.dto.tatum.TransferRequest paymentRequest =
        new org.save.model.dto.tatum.TransferRequest(
            senderAccountId,
            recipientAccount.getLedgerAccountId(),
            transferRequest.getAmount(),
            TransactionType.TRANSFER.name());

    int balanceIsLess = -1;
    BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(transferRequest.getAmount()));
    BigDecimal accountBalance = tatumClient.getBalance(senderAccountId).getAccountBalance();
    if (accountBalance.compareTo(amount) == balanceIsLess) {
      throw new InsufficientBalanceException(
          "Not enough funds. Please check your balance. Now you have "
              + accountBalance
              + " "
              + transferRequest.getCryptoCurrency().name());
    }

    return tatumClient.makeTransfer(paymentRequest);
  }
}
