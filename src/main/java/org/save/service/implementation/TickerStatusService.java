package org.save.service.implementation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.save.model.dto.ticker.TickerStatusRequest;
import org.save.model.entity.social.playlist.TickerStatus;
import org.save.model.enums.TradingStatus;
import org.save.repo.TickerStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TickerStatusService {

  private final TickerStatusRepository tickerStatusRepository;

  public List<TickerStatus> findAll() {
    return tickerStatusRepository.findAll();
  }

  public TickerStatus getTickerStatusByTicker(String ticker) {
    return tickerStatusRepository
        .findByTicker(ticker)
        .orElse(new TickerStatus(ticker, TradingStatus.NOT_SUPPORTED, false));
  }

  public TickerStatus saveOrUpdateStatus(TickerStatusRequest tickerStatusRequest) {
    String ticker = tickerStatusRequest.getTicker().toUpperCase();
    if (tickerStatusRepository.existsByTicker(ticker)) {
      return updateStatus(tickerStatusRequest);
    }
    TickerStatus tickerStatus =
        new TickerStatus(
            ticker,
            tickerStatusRequest.getTradingStatus(),
            tickerStatusRequest.isMarketDataSupported());
    return tickerStatusRepository.save(tickerStatus);
  }

  public TickerStatus updateStatus(TickerStatusRequest tickerStatusRequest) {
    TickerStatus tickerStatus = getTickerStatusByTicker(tickerStatusRequest.getTicker());
    tickerStatus.setTradingStatus(tickerStatusRequest.getTradingStatus());
    tickerStatus.setMarketDataSupported(tickerStatusRequest.isMarketDataSupported());
    return tickerStatusRepository.save(tickerStatus);
  }

  @Transactional
  public void deleteByTicker(String ticker) {
    tickerStatusRepository.deleteByTicker(ticker);
  }
}
