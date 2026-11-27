package com.example.test.common.error;

import java.util.Optional;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.test.common.exception.BusinessException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ErrorResponse> handleBusiness(BusinessException exception) {
        log.warn("Business rule rejected the request: code={}", exception.getErrorCode());
        return ResponseEntity
            .status(exception.getErrorCode().getStatus())
            .body(ErrorResponse.from(exception.getErrorCode()));
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingRequestHeaderException.class})
    ResponseEntity<ErrorResponse> handleInvalidRequest(Exception exception) {
        log.warn("Invalid request: {}", exception.getMessage());
        return ResponseEntity.badRequest().body(ErrorResponse.invalidRequest());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ErrorResponse> handleUnknownPath(NoResourceFoundException exception) {
        log.warn("No operation serves the path: {}", exception.getResourcePath());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.resourceNotFound());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ErrorResponse> handleUnknownMethod(HttpRequestMethodNotSupportedException exception) {
        log.warn("No operation serves the method: {}", exception.getMethod());
        // RFC 9110 requires a 405 to list in Allow the methods that the path serves.
        Set<HttpMethod> allowed = Optional.ofNullable(exception.getSupportedHttpMethods()).orElse(Set.of());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
            .allow(allowed.toArray(HttpMethod[]::new))
            .body(ErrorResponse.methodNotAllowed());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
        log.error("Unexpected failure", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.internal());
    }
}
