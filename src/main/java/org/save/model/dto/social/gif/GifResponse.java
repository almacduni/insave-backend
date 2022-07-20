package org.save.model.dto.social.gif;

import java.util.List;
import lombok.Data;

@Data
public class GifResponse {

  private List<GifDescription> results;
  private String next;
}
