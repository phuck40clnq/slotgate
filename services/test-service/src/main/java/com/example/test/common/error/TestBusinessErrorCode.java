package com.example.test.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TestBusinessErrorCode {

    SIMULATED_BAD_REQUEST("Simulated client error", HttpStatus.BAD_REQUEST),
    SIMULATED_NOT_FOUND("Simulated missing resource", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;

    TestBusinessErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
