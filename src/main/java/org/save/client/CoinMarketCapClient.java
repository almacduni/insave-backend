package org.save.client;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.save.model.dto.coinmarketcap.CryptoResponse;
import org.save.model.dto.coinmarketcap.CryptoTickerDto;
import org.save.model.entity.social.playlist.Ticker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoinMarketCapClient {

  private static final String URL_COINMARKETCAP_API_STOCK_WITH_CRYPTO_TICKERS =
      "https://pro-api.coinmarketcap.com/v1/cryptocurrency/map";

  @Value("${insave.integration.finances.coinmarketcap.api_key}")
  private final String API_KEY;

  private final List<String> currentCrypto = Arrays.asList("BTC", "ETH", "USDT");
  private final RestTemplate restTemplate;

  public CryptoResponse getCryptoTickers() {
    String url = URL_COINMARKETCAP_API_STOCK_WITH_CRYPTO_TICKERS + "?CMC_PRO_API_KEY=" + API_KEY;
    return restTemplate.getForEntity(url, CryptoResponse.class).getBody();
  }

  public Set<Ticker> addTickersFromCMC(Set<Ticker> tickerSet) {
    Set<CryptoTickerDto> cryptoTickersList = getCryptoTickers().getData();
    Set<Ticker> cryptoTickerSet =
        cryptoTickersList.stream()
            .filter(cryptoTicker -> currentCrypto.contains(cryptoTicker.getName()))
            .map(
                cryptoTicker -> {
                  Ticker ticker = new Ticker();
                  ticker.setId(cryptoTicker.getId() + tickerSet.size());
                  ticker.setName(cryptoTicker.getName());
                  ticker.setCompany(cryptoTicker.getCompanyName());
                  // TODO AnalystRecommendation and MarketCapitalization for cryptoTickers
                  return ticker;
                })
            .collect(Collectors.toSet());
    tickerSet.addAll(cryptoTickerSet);
    return tickerSet;
  }
}
