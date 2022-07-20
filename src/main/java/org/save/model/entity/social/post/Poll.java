package org.save.model.entity.social.post;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Poll {

  private Set<Choice> choices;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Choice {

    private String uuid;
    private String title;
    private Set<Long> voterIds;
  }
}
