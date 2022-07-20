package org.save.model.entity.social.playlist;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tickers")
public class Ticker {

  @Id
  @Column(name = "ticker_id", nullable = false, unique = true)
  private Long id;

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "company")
  private String company;

  @Column(name = "market_capitalization")
  private String marketCapitalization;

  @Column(name = "analyst_recommendation")
  private Double analystRecommendation;

  @Column(name = "amg")
  private Double amg;

  @JsonBackReference
  @ManyToMany(mappedBy = "tickers")
  private List<Playlist> playlists;
}
