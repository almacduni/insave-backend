package org.save.model.entity.social.playlist;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.save.model.entity.common.User;
import org.save.model.entity.social.picture.Picture;

@Entity
@Table(name = "playlist")
@Data
@ToString(exclude = {"categories", "tickers", "user"})
@EqualsAndHashCode(exclude = {"categories", "tickers", "user"})
@NoArgsConstructor
@AllArgsConstructor
public class Playlist {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  private String description;
  private String imageURL;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "picture_uuid")
  private Picture picture;

  @Column(name = "openings")
  private Long openings = 0L;

  @Column(name = "tickers_number")
  private Integer tickersNumber;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "user_id")
  private User user;

  @JsonManagedReference
  @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PlaylistTrendingPoints> playlistTrendingPoints;

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JsonManagedReference
  @JoinTable(
      name = "playlist_ticker",
      joinColumns = {@JoinColumn(name = "playlist_id")},
      inverseJoinColumns = {@JoinColumn(name = "ticker_id")})
  private List<Ticker> tickers;

  @JsonBackReference
  @ManyToMany(mappedBy = "playlists")
  private List<PlaylistCategory> categories;

  @ElementCollection
  @CollectionTable(
      name = "playlist_daily_statistic",
      joinColumns = @JoinColumn(name = "playlist_id"))
  private List<PlaylistDailyStatistic> statistic;

  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinTable(
      name = "playList_likes",
      joinColumns = {@JoinColumn(name = "playList_id")},
      inverseJoinColumns = {@JoinColumn(name = "user_id")})
  private Set<User> playListLikes = new HashSet<>();

  public Playlist(String title, String description, String imageURL, List<Ticker> tickers) {
    this.title = title;
    this.description = description;
    this.imageURL = imageURL;
    this.tickers = tickers;
  }
}
