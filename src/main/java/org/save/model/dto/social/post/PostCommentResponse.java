package org.save.model.dto.social.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentResponse {

  private Long id;
  private String date;

  private Long userId;
  private String username;
  private String userLogo;

  private Integer likesCount;
  private Boolean isLiked;

  private String text;
  private String gifUrl;

  private String replyTo;
  private boolean isReply;
}
