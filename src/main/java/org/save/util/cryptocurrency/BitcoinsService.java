package org.save.util.cryptocurrency;

import java.util.Optional;
import org.save.client.TatumClient;
import org.save.model.dto.tatum.CreateLedgerAccountRequest;
import org.save.model.dto.tatum.LedgerAccountDto;
import org.save.model.entity.common.Wallet;
import org.save.model.entity.tatum.WalletAccount;
import org.save.model.enums.CryptoCurrency;
import org.save.repo.CryptoWalletRepository;
import org.save.repo.WalletRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BitcoinsService extends CryptoCurrencyTemplate {

  private final String btcXpub;

  public BitcoinsService(
      WalletRepository walletRepository,
      CryptoWalletRepository accountRepository,
      TatumClient tatumClient,
      @Value("${insave.integration.finances.tatum.bitcoin.wallet.xpub}") String btcXpub) {
    super(walletRepository, accountRepository, tatumClient);
    this.btcXpub = btcXpub;
  }

  @Override
  public String getAddress(Long userId) {
    Wallet wallet = walletRepository.getWalletByUserId(userId);
    Optional<WalletAccount> walletAccount =
        cryptoWalletRepository.findFirstByCryptoCurrencyAndWallet(CryptoCurrency.BTC, wallet);
    return getOptionalAddress(walletAccount, wallet, userId);
  }

  @Override
  protected String createLedgerAccount(WalletAccount account, Long userId, Wallet wallet) {
    CreateLedgerAccountRequest.CustomerRegistration customer =
        new CreateLedgerAccountRequest.CustomerRegistration(userId.toString());
    CreateLedgerAccountRequest ledgerAccountRequest =
        new CreateLedgerAccountRequest(btcXpub, "BTC", customer);
    LedgerAccountDto ledgerAccount = tatumClient.createLedgerAccount(ledgerAccountRequest);
    String accountId = ledgerAccount.getId();
    account.setLedgerAccountId(accountId);
    account.setCryptoCurrency(CryptoCurrency.BTC);
    wallet.setCustomerId(ledgerAccount.getCustomerId());
    return accountId;
  }
}
