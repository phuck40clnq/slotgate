package com.example.auth.common.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.auth.common.exception.BaseException;
import com.example.auth.common.exception.BusinessException;
import com.example.auth.common.exception.InfrastructureException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        log.error(
            "Business error [{}] detail={}",
            ex.getErrorCode(),
            ex.getParams()
        );
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.from(ex));
    }

    @ExceptionHandler(InfrastructureException.class)
    ResponseEntity<ErrorResponse> handleInfra(InfrastructureException ex) {
        log.error(
            "Infra error [{}] detail={}",
            ex.getErrorCode(),
            ex.getParams(),
            ex
        );
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ErrorResponse.from(ex));
    }

    @ExceptionHandler({
        NullPointerException.class,
        IllegalArgumentException.class
    })
    ResponseEntity<?> handleBadRequest(Exception ex) {
        log.error("Bad request", ex);
        return ResponseEntity
            .badRequest()
            .body(ErrorResponse.invalidRequest());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleUnknown(Exception ex) {
        if (ex instanceof BaseException baseException) {
            log.warn("Unhandled exception from base: ", baseException);
        }

        log.error("Unhandled", ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.internal());
    }

}
