package org.save.exception;

public class PlaylistIsAlreadyInCategoryException extends RuntimeException {

  public PlaylistIsAlreadyInCategoryException(String message) {
    super(message);
  }
}
