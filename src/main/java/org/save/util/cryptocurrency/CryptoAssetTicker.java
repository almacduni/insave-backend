package org.save.util.cryptocurrency;

import org.save.exception.NoSuchAssetTickerException;
import org.save.model.enums.CryptoCurrency;

public enum CryptoAssetTicker {
  BTC("BTCUSD", CryptoCurrency.BTC),
  ETH("ETHUSD", CryptoCurrency.ETH),
  CLSH("CLSHUSD", CryptoCurrency.CLSH);

  private final String ticker;
  private final CryptoCurrency cryptoCurrency;

  CryptoAssetTicker(String ticker, CryptoCurrency cryptoCurrency) {
    this.ticker = ticker;
    this.cryptoCurrency = cryptoCurrency;
  }

  public String getTicker() {
    return ticker;
  }

  public CryptoCurrency getCryptoCurrency() {
    return cryptoCurrency;
  }

  public static CryptoAssetTicker valueOfTicker(String ticker) {
    for (CryptoAssetTicker cryptoAssetTicker : values()) {
      if (cryptoAssetTicker.getTicker().equals(ticker)) {
        return cryptoAssetTicker;
      }
    }
    throw new NoSuchAssetTickerException("ticker " + ticker);
  }

  public static CryptoAssetTicker valueOfCurrency(CryptoCurrency currency) {
    for (CryptoAssetTicker cryptoAssetTicker : values()) {
      if (cryptoAssetTicker.getCryptoCurrency() == currency) {
        return cryptoAssetTicker;
      }
    }
    throw new NoSuchAssetTickerException("currency " + currency);
  }

  public static Boolean isExist(String ticker) {
    for (CryptoAssetTicker item : CryptoAssetTicker.values()) {
      if (ticker.equals(item.ticker)) {
        return true;
      }
    }

    return false;
  }
}
