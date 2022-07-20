package org.save.repo;

import java.util.Optional;
import org.save.model.entity.common.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

  Wallet getWalletByUserId(Long userId);

  @Query(
      value =
          "SELECT ledger_account_id "
              + "FROM users "
              + "INNER JOIN cryptowallet_accounts ca "
              + "ON users.wallet_id = ca.wallet_id "
              + "WHERE users.user_id = :userId AND ca.crypto_currency = :crypto",
      nativeQuery = true)
  Optional<String> findLegerAccountByUserIdAndCryptoCurrency(
      @Param("userId") Long userId, @Param("crypto") Integer cryptoCurrency);
}
