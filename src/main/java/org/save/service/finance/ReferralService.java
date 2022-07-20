package org.save.service.finance;

import static org.save.util.DateParseUtils.PATTERN_FOR_CANDLES;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.save.client.TatumClient;
import org.save.model.dto.tatum.PaymentReferenceResponse;
import org.save.model.dto.tatum.TransferRequest;
import org.save.model.entity.common.User;
import org.save.model.entity.common.Wallet;
import org.save.model.entity.tatum.ReferralTransfer;
import org.save.model.entity.tatum.WalletAccount;
import org.save.model.enums.CryptoCurrency;
import org.save.model.enums.ReferralCause;
import org.save.model.enums.TransactionType;
import org.save.repo.CryptoWalletRepository;
import org.save.repo.ReferralTransferRepository;
import org.save.repo.WalletRepository;
import org.save.service.implementation.UserService;
import org.save.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReferralService {

  @Value("${insave.integration.finances.tatum.account_id.for_sending_tokens}")
  private final String TATUM_ACCOUNT_ID_FOR_SENDING_TOKENS; // TODO change

  private final WalletRepository walletRepository;
  private final UserService userService;
  private final CryptoWalletRepository cryptoWalletRepository;
  private final ReferralTransferRepository transferRepository;
  private final TatumClient tatumClient;

  public String createReferralLink() {
    return StringUtils.generateString(8).toString();
  }

  public boolean checkReferralLinkIsValid(String referralLink) {
    return userService.existsUserByReferralLink(referralLink)
        && !userService.isUserInBlacklistByRefLink(referralLink);
  }

  private User getReferralLinkOwner(String referralLink) {
    return userService.findUserByReferralLink(referralLink);
  }

  public void sendTokensByReferralLink(String referralLink) {
    User user = getReferralLinkOwner(referralLink);
    sendTokensFromOurWallet(
        user.getId(),
        ReferralCause.FOR_REFERRAL_REGISTRY.getAmount(),
        ReferralCause.FOR_REFERRAL_REGISTRY);

    // In order to avoid exception on Tatum (429 Too Many Requests)
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void sendTokensFromOurWallet(Long userId, BigDecimal amount, ReferralCause cause) {
    Wallet wallet = walletRepository.getWalletByUserId(userId);
    CryptoCurrency cryptoCurrencyForSend = CryptoCurrency.CLSH; // TODO change ETH to our tokens
    Optional<WalletAccount> cryptoWallet =
        cryptoWalletRepository.findFirstByCryptoCurrencyAndWallet(cryptoCurrencyForSend, wallet);
    cryptoWallet.ifPresent(
        cryptoWallet1 -> {
          TransferRequest paymentRequest =
              new TransferRequest(
                  TATUM_ACCOUNT_ID_FOR_SENDING_TOKENS,
                  cryptoWallet1.getLedgerAccountId(),
                  amount.toString(),
                  TransactionType.REFERRAL.name());
          PaymentReferenceResponse paymentReference = tatumClient.makeTransfer(paymentRequest);
          if (paymentReference != null) {
            log.info(
                amount
                    + " "
                    + cryptoCurrencyForSend.name()
                    + " were transferred user with id "
                    + userId
                    + " "
                    + cause);
            ReferralTransfer referralTransfer =
                new ReferralTransfer(
                    new SimpleDateFormat(PATTERN_FOR_CANDLES).format(new Date()),
                    amount,
                    userId,
                    paymentReference.getReference(),
                    cause.name());
            transferRepository.save(referralTransfer);
          }
        });
  }
}
