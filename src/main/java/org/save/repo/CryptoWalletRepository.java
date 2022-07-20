package org.save.repo;

import java.util.Optional;
import org.save.model.entity.common.Wallet;
import org.save.model.entity.tatum.WalletAccount;
import org.save.model.enums.CryptoCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoWalletRepository extends JpaRepository<WalletAccount, Long> {

  Optional<WalletAccount> findFirstByCryptoCurrencyAndWallet(
      CryptoCurrency cryptoCurrency, Wallet wallet);

  Optional<WalletAccount> findFirstByLedgerAccountId(String account_id);
}
