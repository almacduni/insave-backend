package org.save.model.dto.tatum;

import lombok.Data;

@Data
public class TatumResponse {

  private String id;
  private String errorCode;
  private String message;
  private Integer statusCode;
}
