package com.example.auth.common.exception;

import java.util.Map;

import com.example.auth.common.error.ErrorCode;

public abstract class BaseException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, Object> params;

    protected BaseException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
        this.params = null;
    }

    protected BaseException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.message(), cause);
        this.errorCode = errorCode;
        this.params = null;
    }

    protected BaseException(ErrorCode errorCode, Map<String, Object> params) {
        super(errorCode.message());
        this.errorCode = errorCode;
        this.params = params;
    }

    protected BaseException(
        ErrorCode errorCode,
        Map<String, Object> params,
        Throwable cause
    ) {
        super(errorCode.message(), cause);
        this.errorCode = errorCode;
        this.params = params;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }
}
