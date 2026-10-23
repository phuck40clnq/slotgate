package com.example.auth.common.error;

import org.slf4j.MDC;

import com.example.auth.common.exception.BaseException;

public record ErrorResponse(
    String code,
    String message,
    String traceId
) {
    public static ErrorResponse from(BaseException exception) {
        var errorCode = exception.getErrorCode();
        return new ErrorResponse(
            errorCode.code(),
            errorCode.message(),
            MDC.get("traceId")
        );
    }

    public static ErrorResponse invalidRequest() {
        return new ErrorResponse(
            "INVALID_REQUEST_BODY",
            "Invalid request",
            MDC.get("traceId")
        );
    }

    public static ErrorResponse internal() {
        return new ErrorResponse(
            "INTERNAL_ERROR_BODY",
            "Internal error",
            MDC.get("traceId")
        );
    }
}
