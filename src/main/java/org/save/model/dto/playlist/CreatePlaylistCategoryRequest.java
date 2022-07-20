package org.save.model.dto.playlist;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePlaylistCategoryRequest {

  @NotBlank
  @Length(min = 3)
  private String name;
}
