package com.example.seatinventory.common.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.seatinventory.common.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ErrorResponse> handleBusiness(BusinessException exception) {
        log.warn("Business rule rejected the request: code={}, params={}",
            exception.getErrorCode(), exception.getParams());
        return ResponseEntity
            .status(exception.getErrorCode().getStatus())
            .body(ErrorResponse.from(exception.getErrorCode()));
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        MethodArgumentTypeMismatchException.class,
        MissingRequestHeaderException.class,
        HttpMessageNotReadableException.class
    })
    ResponseEntity<ErrorResponse> handleInvalidRequest(Exception exception) {
        log.warn("Invalid request: {}", exception.getMessage());
        return ResponseEntity.badRequest().body(ErrorResponse.invalidRequest());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
        log.error("Unexpected failure", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.internal());
    }
}
