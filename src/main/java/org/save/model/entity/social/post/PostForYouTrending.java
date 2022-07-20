package org.save.model.entity.social.post;

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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "post_foryou_trending")
public class PostForYouTrending {

  @Id
  @Column(name = "trending_id", nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @Column(name = "points", nullable = false)
  private Long points;

  public static final Comparator<PostForYouTrending> PointsComparator =
      (e1, e2) -> e2.getPoints().compareTo(e1.getPoints());
}
