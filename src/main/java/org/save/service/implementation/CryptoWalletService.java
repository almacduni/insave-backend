package org.save.service.implementation;

import java.math.BigDecimal;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.save.client.TatumClient;
import org.save.exception.InsufficientBalanceException;
import org.save.exception.NoSuchObjectException;
import org.save.model.dto.tatum.BtcWithdrawRequest;
import org.save.model.dto.tatum.EthWithdrawRequest;
import org.save.model.dto.tatum.WithdrawRequest;
import org.save.model.dto.tatum.WithdrawResponse;
import org.save.model.entity.common.User;
import org.save.model.entity.common.Wallet;
import org.save.model.entity.tatum.WalletAccount;
import org.save.model.enums.CryptoCurrency;
import org.save.repo.CryptoWalletRepository;
import org.save.repo.WalletRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class CryptoWalletService {

  private final WalletRepository walletRepository;
  private final CryptoWalletRepository cryptoWalletRepository;
  private final WalletService walletService;
  private final TatumClient tatumClient;
  private final CodeSender codeSender;
  private final UserService userService;

  @Value("${insave.integration.finances.tatum.ethereum.wallet.signatureId}")
  private final String ethSignatureId;

  @Value("${insave.integration.finances.tatum.bitcoin.wallet.signatureId}")
  private final String btcSignatureId;

  @Value("${insave.integration.finances.tatum.bitcoin.wallet.xpub}")
  private final String btcXpub;

  public void sendSecurityCode(WithdrawRequest request, CryptoCurrency currency) {
    User user = userService.getUserById(request.getUserId());
    checkBalanceForWithdraw(request, currency);

    codeSender.send(user.getUsername(), user.getEmail(), "Security code");
  }

  public WithdrawResponse withdrawEth(WithdrawRequest request, int code) {
    checkingBeforeWithdraw(request, code, CryptoCurrency.ETH);

    String senderAccountId = getSenderAccountId(request.getUserId(), CryptoCurrency.ETH);
    int ethIndex =
        cryptoWalletRepository.findFirstByLedgerAccountId(senderAccountId).get().getIndex();
    EthWithdrawRequest mainRequest =
        new EthWithdrawRequest(
            senderAccountId, request.getAddress(), request.getAmount(), ethSignatureId, ethIndex);

    return tatumClient.withdrawEthereum(mainRequest);
  }

  public WithdrawResponse withdrawBtc(WithdrawRequest request, int code) {
    checkingBeforeWithdraw(request, code, CryptoCurrency.BTC);

    String senderAccountId = getSenderAccountId(request.getUserId(), CryptoCurrency.BTC);

    BtcWithdrawRequest mainRequest =
        new BtcWithdrawRequest(
            senderAccountId, request.getAddress(), request.getAmount(), btcSignatureId, btcXpub);

    return tatumClient.withdrawBitcoin(mainRequest);
  }

  private String getSenderAccountId(Long id, CryptoCurrency currency) {
    Wallet wallet = walletRepository.getWalletByUserId(id);
    return cryptoWalletRepository
        .findFirstByCryptoCurrencyAndWallet(currency, wallet)
        .map(WalletAccount::getLedgerAccountId)
        .orElseThrow(() -> new NoSuchObjectException("Wallet with id " + id + " not found"));
  }

  private void checkBalanceForWithdraw(WithdrawRequest request, CryptoCurrency currency) {
    BigDecimal balance =
        walletService
            .getCryptoBalanceFromTatumByUserId(request.getUserId(), currency)
            .getAvailableBalance();

    Double amount = Double.parseDouble(request.getAmount());
    Double fee = (currency == CryptoCurrency.BTC) ? 0.0005 : 0.0;

    if (amount + fee > balance.doubleValue()) {
      throw new InsufficientBalanceException("Not enough funds. Please check your balance");
    }
  }

  private void checkingBeforeWithdraw(WithdrawRequest request, int code, CryptoCurrency currency) {
    User user = userService.getUserById(request.getUserId());

    if (!codeSender.codeIsValid(user.getUsername(), code)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid activation code");
    }

    checkBalanceForWithdraw(request, currency);

    codeSender.remove(user.getUsername());
  }
}
