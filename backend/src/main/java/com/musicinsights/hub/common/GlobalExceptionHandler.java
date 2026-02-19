package com.musicinsights.hub.common;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AppException.class)
  public ResponseEntity<ApiError> handleAppException(AppException ex) {
    return ResponseEntity.status(ex.getStatus())
        .body(new ApiError(ex.getErrorCode(), ex.getMessage(), ex.getDetails()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
    List<String> details = ex.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .toList();
    return ResponseEntity.badRequest().body(new ApiError("VALIDATION_ERROR", "Validation failed", details));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex) {
    List<String> details = ex.getConstraintViolations().stream().map(v -> v.getMessage()).toList();
    return ResponseEntity.badRequest().body(new ApiError("VALIDATION_ERROR", "Validation failed", details));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleUnhandled(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApiError("INTERNAL_ERROR", "Unexpected error", List.of(ex.getMessage())));
  }
}
