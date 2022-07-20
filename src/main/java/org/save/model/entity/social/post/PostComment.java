package org.save.model.entity.social.post;

import java.time.Instant;
import java.util.Collection;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.save.model.entity.common.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "post_comments")
public class PostComment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "comment_id", nullable = false)
  private Long id;

  @Column(name = "date", nullable = false)
  private Instant date;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @OneToMany(
      mappedBy = "comment",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.EAGER)
  private Collection<PostCommentLike> likes;

  @Column(name = "text", length = 140)
  private String text;

  private boolean isReply;

  @Column private String gifUrl;

  @Column(name = "reply_to")
  private String replyTo;
}
