package com.example.test.common.error;

import org.slf4j.MDC;

public record ErrorResponse(String code, String message, String traceId) {

    public static ErrorResponse from(TestBusinessErrorCode errorCode) {
        return new ErrorResponse(errorCode.name(), errorCode.getMessage(), MDC.get("traceId"));
    }

    public static ErrorResponse invalidRequest() {
        return new ErrorResponse("INVALID_REQUEST", "Invalid request", MDC.get("traceId"));
    }

    public static ErrorResponse resourceNotFound() {
        return new ErrorResponse("RESOURCE_NOT_FOUND", "No operation serves this path", MDC.get("traceId"));
    }

    public static ErrorResponse methodNotAllowed() {
        return new ErrorResponse("METHOD_NOT_ALLOWED", "This path does not serve this method", MDC.get("traceId"));
    }

    public static ErrorResponse internal() {
        return new ErrorResponse("INTERNAL_ERROR", "Internal error", MDC.get("traceId"));
    }
}
