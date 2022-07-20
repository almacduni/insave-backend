package org.save.util.cryptocurrency;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.save.model.dto.tatum.CustomerTradeMatchRequest;
import org.save.model.dto.tatum.IncomingTransactionRequest;
import org.save.model.entity.tatum.WalletAccount;
import org.save.model.enums.ReferralCause;
import org.save.repo.CryptoWalletRepository;
import org.save.service.finance.ReferralService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CryptoService {

  private final CryptoWalletRepository cryptoWalletRepository;
  private final ReferralService referralService;

  public void foundAccountWithCurrency(IncomingTransactionRequest transaction) {
    log.info("Incoming transaction event {} ", transaction);
  }

  public void foundAccountsWithOrder(CustomerTradeMatchRequest customerTradeMatch) {
    log.info("Closed trade order event {} ", customerTradeMatch);
    Optional<WalletAccount> walletAccount1 =
        cryptoWalletRepository.findFirstByLedgerAccountId(
            customerTradeMatch.getCurrency1AccountId());
    walletAccount1.ifPresent(
        account ->
            referralService.sendTokensFromOurWallet(
                walletAccount1.get().getWallet().getUser().getId(),
                ReferralCause.FOR_CRYPTO_TRADE_ORDER.getAmount(),
                ReferralCause.FOR_CRYPTO_TRADE_ORDER));
  }
}
