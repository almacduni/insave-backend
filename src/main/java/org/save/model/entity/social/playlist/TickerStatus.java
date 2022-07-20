package org.save.model.entity.social.playlist;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.save.model.enums.TradingStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class TickerStatus {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String ticker;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private TradingStatus tradingStatus;

  @Column(
      name = "market_data_supported",
      nullable = false,
      columnDefinition = "boolean default false")
  private boolean isMarketDataSupported;

  public TickerStatus(String ticker, TradingStatus tradingStatus, boolean isMarketDataSupported) {
    this.ticker = ticker;
    this.tradingStatus = tradingStatus;
    this.isMarketDataSupported = isMarketDataSupported;
  }
}
