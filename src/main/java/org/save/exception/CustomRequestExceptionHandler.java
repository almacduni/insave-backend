package org.save.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.save.model.dto.error.ErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class CustomRequestExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException exception,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    Map<String, String> errors = new HashMap<>();

    exception
        .getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(LimitOnPostsAndPostsCommentsException.class)
  protected ResponseEntity<ErrorResponse> handleLimitOnPostsAndPostsCommentsException(
      LimitOnPostsAndPostsCommentsException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse = buildErrorResponse(exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(ReportAlreadyExistException.class)
  protected ResponseEntity<ErrorResponse> handleReportAlreadyExistException(
      ReportAlreadyExistException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse = buildErrorResponse(exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(NoCredentialsException.class)
  protected ResponseEntity<ErrorResponse> handleNoCredentialsException(
      NoCredentialsException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse = buildErrorResponse(exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(UserNotAuthenticatedException.class)
  protected ResponseEntity<ErrorResponse> handleUserNotAuthenticatedException(
      UserNotAuthenticatedException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.UNAUTHORIZED;
    var errorResponse = buildErrorResponse(exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(PlaylistIsAlreadyInCategoryException.class)
  protected ResponseEntity<ErrorResponse> handlePlaylistIsAlreadyInCategory(
      PlaylistIsAlreadyInCategoryException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse = buildErrorResponse(exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(PlaylistCategoryExistsException.class)
  protected ResponseEntity<ErrorResponse> handlePlaylistCategoryExists(
      PlaylistCategoryExistsException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse = buildErrorResponse(exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(InvalidArgumentException.class)
  protected ResponseEntity<ErrorResponse> handleInvalidArgumentException(
      InvalidArgumentException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse = buildErrorResponse(exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(EmailIsInUseException.class)
  protected ResponseEntity<ErrorResponse> handleEmailIsInUseException(
      EmailIsInUseException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse = buildErrorResponse(exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(PasswordIsInUseException.class)
  protected ResponseEntity<ErrorResponse> handlePasswordIsInUseException(
      PasswordIsInUseException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse = buildErrorResponse(exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(IncorrectPasswordException.class)
  protected ResponseEntity<ErrorResponse> handleIncorrectPasswordException(
      IncorrectPasswordException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse = buildErrorResponse(exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(InsufficientBalanceException.class)
  protected ResponseEntity<ErrorResponse> handleFolderNotFoundException(
      InsufficientBalanceException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse = buildErrorResponse(exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(InvalidTickerException.class)
  protected ResponseEntity<ErrorResponse> handleFolderNotFoundException(
      InvalidTickerException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse = buildErrorResponse(exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(ResponseStatusException.class)
  protected ResponseEntity<ErrorResponse> handleResponseStatusException(
      ResponseStatusException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.UNAUTHORIZED;
    var errorResponse = buildErrorResponse(exception.getReason(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(NoSuchObjectException.class)
  protected ResponseEntity<ErrorResponse> handleResponseStatusException(
      NoSuchObjectException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse = buildErrorResponse(exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(WikipediaParserException.class)
  protected ResponseEntity<ErrorResponse> handleResponseStatusException(
      WikipediaParserException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse = buildErrorResponse(exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ErrorResponse> handleMaxSizeException(
      MaxUploadSizeExceededException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse = buildErrorResponse("Unable to upload. File is too large!", status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(FileUploadException.class)
  public ResponseEntity<ErrorResponse> handleFileUploadException(FileUploadException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse =
        buildErrorResponse("Could not upload the file: " + exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(NoSuchAssetTickerException.class)
  public ResponseEntity<ErrorResponse> handleNoSuchAssetTickerException(
      NoSuchAssetTickerException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse =
        buildErrorResponse("Asset ticker not found! Bad: " + exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(TatumTradeException.class)
  public ResponseEntity<ErrorResponse> handleTatumTradeException(TatumTradeException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse =
        buildErrorResponse("Unable to create trade order: " + exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(TatumCancelTradeException.class)
  public ResponseEntity<ErrorResponse> handleTatumCancelTradeException(
      TatumCancelTradeException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse =
        buildErrorResponse("Unable to cancel trade order: " + exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(NotUniqueUsernameException.class)
  public ResponseEntity<ErrorResponse> handleNotUniqueUsernameException(
      NotUniqueUsernameException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse = buildErrorResponse(exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(InvalidAvatarFileTypeException.class)
  public ResponseEntity<ErrorResponse> handleInvalidAvatarFileTypeException(
      InvalidAvatarFileTypeException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse = buildErrorResponse(exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(InvalidLinkException.class)
  public ResponseEntity<ErrorResponse> handleInvalidLinkException(InvalidLinkException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.BAD_REQUEST;
    var errorResponse = buildErrorResponse(exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<ErrorResponse> handleInvalidTokenException(
      InvalidTokenException exception) {
    log.error("Error Details:", exception);
    var status = HttpStatus.UNAUTHORIZED;
    var errorResponse = buildErrorResponse(exception.getMessage(), status);
    return new ResponseEntity<>(errorResponse, status);
  }

  private ErrorResponse buildErrorResponse(String message, HttpStatus httpStatus) {
    return ErrorResponse.builder()
        .error(httpStatus.getReasonPhrase())
        .message(message)
        .status(httpStatus.value())
        .timestamp(Instant.now().toString())
        .build();
  }
}
