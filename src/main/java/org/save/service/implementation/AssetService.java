package org.save.service.implementation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.save.model.entity.common.Asset;
import org.save.model.entity.common.Portfolio;
import org.save.model.entity.common.Wallet;
import org.save.model.entity.tatum.WalletAccount;
import org.save.model.enums.CryptoCurrency;
import org.save.repo.AssetRepository;
import org.save.repo.WalletRepository;
import org.save.util.cryptocurrency.CryptoAssetTicker;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AssetService {

  private final AssetRepository assetRepository;
  private final WalletRepository walletRepository;
  private final WalletService walletService;

  public Asset createAsset(Asset asset) {
    return assetRepository.save(asset);
  }

  public void updateCryptoAssets(Portfolio portfolio) {
    Map<CryptoCurrency, Asset> cryptoAssets =
        portfolio.getAssets().stream()
            .filter(asset -> CryptoAssetTicker.isExist(asset.getTicker()))
            .collect(
                Collectors.toMap(
                    asset -> {
                      CryptoAssetTicker cryptoAssetTicker =
                          CryptoAssetTicker.valueOfTicker(asset.getTicker());
                      return cryptoAssetTicker.getCryptoCurrency();
                    },
                    asset -> asset));

    Long userId = portfolio.getUser().getId();
    Wallet wallet = walletRepository.getWalletByUserId(userId);
    List<WalletAccount> walletAccounts = wallet.getAccounts();

    walletAccounts.forEach(
        account -> {
          CryptoCurrency cryptoCurrency = account.getCryptoCurrency();
          Asset asset = cryptoAssets.get(cryptoCurrency);
          if (asset == null) {
            asset = createCryptoAsset(portfolio, CryptoAssetTicker.valueOfCurrency(cryptoCurrency));
          }
          asset.setAmount(
              walletService
                  .getCryptoBalanceFromTatumByUserId(userId, account.getCryptoCurrency())
                  .getAccountBalance());

          assetRepository.save(asset);
        });
  }

  public Asset createCryptoAsset(Portfolio portfolio, CryptoAssetTicker ticker) {
    Asset asset = new Asset();
    asset.setTotalPrice(BigDecimal.ZERO);
    asset.setAmount(BigDecimal.ZERO);
    asset.setAveragePrice(BigDecimal.ZERO);
    asset.setTicker(ticker.getTicker());
    asset.setPortfolio(portfolio);
    return createAsset(asset);
  }
}
