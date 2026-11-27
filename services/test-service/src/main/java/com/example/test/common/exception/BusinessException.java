package com.example.test.common.exception;

import java.util.Objects;

import lombok.Getter;

import com.example.test.common.error.TestBusinessErrorCode;

@Getter
public class BusinessException extends RuntimeException {

    private final TestBusinessErrorCode errorCode;

    public BusinessException(TestBusinessErrorCode errorCode) {
        super(Objects.requireNonNull(errorCode, "errorCode").getMessage());
        this.errorCode = errorCode;
    }
}
