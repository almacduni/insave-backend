package org.save.model.entity.watchlist;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class WatchlistItemDescription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String ticker;
  private String logo;
  private String description;

  public WatchlistItemDescription(String ticker, String description, String logo) {
    this.ticker = ticker;
    this.description = description;
    this.logo = logo;
  }
}
