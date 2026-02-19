package com.musicinsights.hub.common;

import java.util.List;
import org.springframework.http.HttpStatus;

public class AppException extends RuntimeException {

  private final String errorCode;
  private final HttpStatus status;
  private final List<String> details;

  public AppException(String errorCode, String message, HttpStatus status, List<String> details) {
    super(message);
    this.errorCode = errorCode;
    this.status = status;
    this.details = details;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public List<String> getDetails() {
    return details;
  }
}
