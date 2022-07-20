package org.save.exception;

public class EmailIsInUseException extends RuntimeException {

  public EmailIsInUseException(String message) {
    super(message);
  }
}
