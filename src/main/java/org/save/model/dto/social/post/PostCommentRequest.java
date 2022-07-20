package org.save.model.dto.social.post;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentRequest {

  @NotNull private Long postId;

  @NotBlank
  @Size(max = 140)
  private String text;

  private Boolean isReply;
  private String replyTo;
  private String gifUrl;
}
