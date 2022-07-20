package org.save.exception;

public class PasswordIsInUseException extends RuntimeException {

  public PasswordIsInUseException(String message) {
    super(message);
  }
}
