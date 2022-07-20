package org.save.util.cryptocurrency;

import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.save.client.TatumClient;
import org.save.model.dto.tatum.AddressDto;
import org.save.model.dto.tatum.CreateSubscriptionRequest;
import org.save.model.dto.tatum.SubscriptionRequestAttribute;
import org.save.model.entity.common.Wallet;
import org.save.model.entity.tatum.WalletAccount;
import org.save.repo.CryptoWalletRepository;
import org.save.repo.WalletRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public abstract class CryptoCurrencyTemplate {

  static String INSAVE_URL;
  static String SUBSCRIPTION_TRANSACTIONAL_URL;
  static String SUBSCRIPTION_ORDER_URL;

  final WalletRepository walletRepository;
  final CryptoWalletRepository cryptoWalletRepository;
  final TatumClient tatumClient;

  @Value(value = "${insave.env.url}")
  private String insaveUrl;

  @PostConstruct
  public void init() {
    INSAVE_URL = insaveUrl;
    SUBSCRIPTION_TRANSACTIONAL_URL = INSAVE_URL + "/crypto/transaction";
    SUBSCRIPTION_ORDER_URL = INSAVE_URL + "/crypto/order";
  }

  public abstract String getAddress(Long userId);

  abstract String createLedgerAccount(WalletAccount account, Long userId, Wallet wallet);

  String getOptionalAddress(Optional<WalletAccount> walletAccount, Wallet wallet, Long userId) {
    String address;
    if (walletAccount.isPresent()) {
      WalletAccount account = walletAccount.get();
      address = account.getAddress();
    } else {
      WalletAccount newAccount = new WalletAccount();
      newAccount.setWallet(wallet);
      address = createAddress(wallet, newAccount, userId);
      cryptoWalletRepository.save(newAccount);
    }
    return address;
  }

  String createAddress(Wallet wallet, WalletAccount account, Long userId) {
    String ledgerAccountId = createLedgerAccount(account, userId, wallet);
    createSubscriptionForTransactions(ledgerAccountId);
    createSubscriptionForOrders(wallet.getCustomerId());
    AddressDto accountAddress = tatumClient.createAddress(ledgerAccountId);
    String address = accountAddress.getAddress();
    account.setAddress(address);
    account.setIndex(accountAddress.getDerivationKey());
    wallet.addAccountToWallet(account);
    return address;
  }

  // This subscription is created for each cryptocurrency
  void createSubscriptionForTransactions(String ledgerAccountId) {
    SubscriptionRequestAttribute requestAttribute =
        new SubscriptionRequestAttribute(ledgerAccountId, SUBSCRIPTION_TRANSACTIONAL_URL);
    CreateSubscriptionRequest subscriptionRequest =
        new CreateSubscriptionRequest("ACCOUNT_INCOMING_BLOCKCHAIN_TRANSACTION", requestAttribute);
    int statusCode = tatumClient.createSubscription(subscriptionRequest);
    log.info("Create subscription for transactions status code: {}", statusCode);
  }

  // This subscription is created only once
  void createSubscriptionForOrders(String customerId) {
    SubscriptionRequestAttribute requestAttribute =
        new SubscriptionRequestAttribute(customerId, SUBSCRIPTION_ORDER_URL);
    CreateSubscriptionRequest subscriptionRequest =
        new CreateSubscriptionRequest("CUSTOMER_TRADE_MATCH", requestAttribute);
    int statusCode = tatumClient.createSubscription(subscriptionRequest);
    log.info("Create subscription for orders status code: {}", statusCode);
  }
}
