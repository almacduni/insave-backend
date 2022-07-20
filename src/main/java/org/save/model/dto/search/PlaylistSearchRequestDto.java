package org.save.model.dto.search;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.save.model.entity.social.picture.Picture;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlaylistSearchRequestDto extends CommonSearchRequestDto {

  private String title;
  private Picture picture;
}
