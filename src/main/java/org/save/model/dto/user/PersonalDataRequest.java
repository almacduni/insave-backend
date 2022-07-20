package org.save.model.dto.user;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class PersonalDataRequest {

  @NotBlank
  @Pattern(regexp = "^[a-zA-Z]+$", message = "{firstName.not.valid}")
  @Size(min = 2, max = 50)
  private String firstName;

  @NotBlank
  @Pattern(regexp = "^[a-zA-Z]+$", message = "{lastName.not.valid}")
  @Size(min = 2, max = 50)
  private String lastName;

  @NotBlank
  @Pattern(regexp = "^[0-9]{1,2}-[0-9]{1,2}-[0-9]{4}$", message = "{date.not.valid}")
  private String date;

  @NotBlank private String streetAddress;

  @NotBlank private String postcode;

  @NotBlank private String city;

  @NotBlank private List<String> pictures;
}
