package com.example.user.common.error;

import org.slf4j.MDC;

import com.example.user.common.exception.BaseException;

public record ErrorResponse(
    String code,
    String message,
    String traceId
) {
    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(
            errorCode.code(),
            errorCode.message(),
            MDC.get("traceId")
        );
    }

    public static ErrorResponse from(BaseException exception) {
        return from(exception.getErrorCode());
    }

}
