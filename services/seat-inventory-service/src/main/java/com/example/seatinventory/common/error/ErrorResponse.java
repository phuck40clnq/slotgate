package com.example.seatinventory.common.error;

import org.slf4j.MDC;

public record ErrorResponse(String code, String message, String traceId) {

    public static ErrorResponse from(SeatInventoryBusinessErrorCode errorCode) {
        return new ErrorResponse(errorCode.name(), errorCode.getMessage(), MDC.get("traceId"));
    }

    public static ErrorResponse invalidRequest() {
        return new ErrorResponse("INVALID_REQUEST", "Invalid request", MDC.get("traceId"));
    }

    public static ErrorResponse internal() {
        return new ErrorResponse("INTERNAL_ERROR", "Internal error", MDC.get("traceId"));
    }
}
