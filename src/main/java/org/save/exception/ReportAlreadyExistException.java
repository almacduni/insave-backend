package org.save.exception;

public class ReportAlreadyExistException extends RuntimeException {

  public ReportAlreadyExistException() {
    super("Report on this post or comment is already exist");
  }
}
