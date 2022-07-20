package org.save.model.dto.social.post;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

  private Long id;
  private String date;

  private Long userId;
  private String username;
  private String userLogo;

  private Integer likesCount;
  private Integer commentsCount;
  private Boolean isLiked;

  private String text;
  private String videoUrl;
  private PollResponse poll;
  private List<String> pictures;
  private List<PostCommentResponse> postComments;
  private String gifUrl;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PollResponse {

    private Set<ChoiceResponse> choices;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChoiceResponse {

      private String id;
      private String title;
      private Integer votes;
      private Boolean isVoted;
    }
  }
}
