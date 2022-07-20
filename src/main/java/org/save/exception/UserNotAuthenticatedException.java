package org.save.exception;

public class UserNotAuthenticatedException extends RuntimeException {

  public UserNotAuthenticatedException() {
    super("User is not authenticated");
  }
}
