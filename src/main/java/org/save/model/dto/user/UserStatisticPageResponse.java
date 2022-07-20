package org.save.model.dto.user;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatisticPageResponse {

  private BigDecimal tokenCount;
  private BigDecimal usdCount;
  private BigDecimal usdEarned;
  private Long changesPercentage;

  private Long friendsInvitedCount;
  private BigDecimal friendsEarnCount;

  private Long postsPublishedCount;
  private BigDecimal postsEarnCount;
  private Long postsLikes;
  private Long postsComments;

  private Long videoPublishedCount;
  private BigDecimal videoEarnCount;
  private Long videoLikes;
  private Long videoComments;
}
