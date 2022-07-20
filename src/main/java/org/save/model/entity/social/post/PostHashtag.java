package org.save.model.entity.social.post;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO: delete if doesnt needed
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "post_hashtags")
public class PostHashtag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "hashtag_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @Column(name = "name", length = 840, nullable = false)
  private String name;
}
