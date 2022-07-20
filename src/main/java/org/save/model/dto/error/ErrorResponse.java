package org.save.model.dto.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

  private String error;
  private String message;
  private Integer status;
  private String timestamp;
}
