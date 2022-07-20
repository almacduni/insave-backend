package org.save.exception;

public class NoSuchAssetTickerException extends RuntimeException {
  public NoSuchAssetTickerException(String message) {
    super(message);
  }
}
