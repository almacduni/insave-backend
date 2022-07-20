package org.save.service.playlist;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.save.client.CoinMarketCapClient;
import org.save.model.entity.social.playlist.Ticker;
import org.save.repo.TickerRepository;
import org.save.util.PolygonAPI;
import org.save.util.parsers.FinvizParser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

  private final TickerRepository tickerRepository;
  private final FinvizParser finvizParser;
  private final CoinMarketCapClient coinMarketCapClient;
  private final PolygonAPI polygonAPI;
  private List<Ticker> tempSearchTickerResponseList;

  @Scheduled(cron = "0 0 3 * * *", zone = "Europe/London")
  public void fetchAndSaveTickers() {
    tempSearchTickerResponseList = tickerRepository.findAll();
    final Set<Ticker> tickerSet = finvizParser.getTickers();
    final Set<Ticker> tickerSetWithCrypto = coinMarketCapClient.addTickersFromCMC(tickerSet);
    tickerRepository.deleteAll();
    tickerRepository.saveAll(tickerSetWithCrypto);
    try {
      tempSearchTickerResponseList.clear();
    } catch (UnsupportedOperationException e) {
      log.error("Error from tempSearchTickerResponseList", e);
    }
  }

  public void updateAllAmgByTickerName() {
    List<Ticker> tickerListWithCrypto = tickerRepository.findAll();
    tickerListWithCrypto.forEach(
        ticker -> {
          double amg = polygonAPI.getTickerAMG(ticker.getName()).doubleValue();
          tickerRepository.setAmgByTickerName(amg, ticker.getName());
        });
  }
}
