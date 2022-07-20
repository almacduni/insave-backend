package org.save.util.cryptocurrency;

import java.util.Optional;
import lombok.SneakyThrows;
import org.save.client.TatumClient;
import org.save.model.dto.tatum.CreateLedgerAccountRequest;
import org.save.model.dto.tatum.LedgerAccountDto;
import org.save.model.entity.common.Wallet;
import org.save.model.entity.tatum.WalletAccount;
import org.save.model.enums.CryptoCurrency;
import org.save.repo.CryptoWalletRepository;
import org.save.repo.WalletRepository;
import org.springframework.stereotype.Component;

@Component
public class TokenService extends CryptoCurrencyTemplate {

  public TokenService(
      WalletRepository walletRepository,
      CryptoWalletRepository accountRepository,
      TatumClient tatumClient) {
    super(walletRepository, accountRepository, tatumClient);
  }

  @Override
  public String getAddress(Long userId) {
    Wallet wallet = walletRepository.getWalletByUserId(userId);
    Optional<WalletAccount> walletAccount =
        cryptoWalletRepository.findFirstByCryptoCurrencyAndWallet(CryptoCurrency.CLSH, wallet);
    return getOptionalAddress(walletAccount, wallet, userId);
  }

  @Override
  @SneakyThrows
  String createAddress(Wallet wallet, WalletAccount account, Long userId) {
    String ledgerAccountId = createLedgerAccount(account, userId, wallet);
    createSubscriptionForTransactions(ledgerAccountId);
    // In order to avoid exception on Tatum (429 Too Many Requests)
    Thread.sleep(1000);
    WalletAccount ethAccount = getEthAccount(wallet);
    String ethAddress = ethAccount.getAddress();

    tatumClient.assignAddressForAccount(ledgerAccountId, ethAddress);
    account.setAddress(ethAddress);
    account.setIndex(ethAccount.getIndex());
    wallet.addAccountToWallet(account);
    return ethAddress;
  }

  @Override
  String createLedgerAccount(WalletAccount account, Long userId, Wallet wallet) {
    CreateLedgerAccountRequest.CustomerRegistration customer =
        new CreateLedgerAccountRequest.CustomerRegistration(userId.toString());
    CreateLedgerAccountRequest ledgerAccountRequest =
        new CreateLedgerAccountRequest(null, "CLSH", customer);
    LedgerAccountDto ledgerAccount = tatumClient.createLedgerAccount(ledgerAccountRequest);
    String accountId = ledgerAccount.getId();
    account.setLedgerAccountId(accountId);
    account.setCryptoCurrency(CryptoCurrency.CLSH);
    wallet.setCustomerId(ledgerAccount.getCustomerId());
    return accountId;
  }

  private WalletAccount getEthAccount(Wallet wallet) {
    Optional<WalletAccount> walletAccount =
        cryptoWalletRepository.findFirstByCryptoCurrencyAndWallet(CryptoCurrency.ETH, wallet);
    return walletAccount.get();
  }
}
