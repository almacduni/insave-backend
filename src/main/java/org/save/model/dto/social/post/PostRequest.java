package org.save.model.dto.social.post;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

  private String text;
  private List<String> poll;
  private String gifUrl;
}
