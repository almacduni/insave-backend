package org.save.model.entity.social.post;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.save.model.entity.common.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "posts")
@TypeDef(name = "json", typeClass = JsonBinaryType.class)
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "post_id", nullable = false)
  private Long id;

  @Column(name = "date", nullable = false)
  private Instant date;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private Collection<PostLike> likes;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private Collection<PostComment> comments;

  @Column(name = "text", length = 840)
  private String text;

  @Column(name = "video_url")
  private String videoUrl;

  @Type(type = "json")
  @Column(name = "poll", columnDefinition = "json")
  private Poll poll;

  @ElementCollection
  @CollectionTable(name = "post_daily_statistic", joinColumns = @JoinColumn(name = "post_id"))
  private List<PostDailyStatistic> trending;

  @ElementCollection private List<String> pictures;

  @Column private String gifUrl;
}
