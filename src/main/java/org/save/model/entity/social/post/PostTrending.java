package org.save.model.entity.social.post;

import java.util.Comparator;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "post_trendings")
public class PostTrending {

  @Id
  @Column(name = "trending_id", nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @Column(name = "points", nullable = false)
  private Long points;

  public static final Comparator<PostTrending> PointsComparator =
      (e1, e2) -> e2.getPoints().compareTo(e1.getPoints());
}
