package org.save.model.entity.social.playlist;

import java.util.Comparator;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** class <code>PlaylistTop</code> saves points we want to sort by in category 'Top' */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "playlist_trending_points")
public class PlaylistTrendingPoints {

  @Id
  @Column(name = "trending_id", nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "playlist_id", nullable = false)
  private Playlist playlist;

  @Column private Long topPoints = 0L;

  @Column private Long trendingPoints = 0L;

  public static final Comparator<PlaylistTrendingPoints> TopPointsComparator =
      (e1, e2) -> e2.getTopPoints().compareTo(e1.getTopPoints());
}
