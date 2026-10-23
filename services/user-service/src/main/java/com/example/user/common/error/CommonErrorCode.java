package com.example.user.common.error;

public enum CommonErrorCode implements ErrorCode {
    
    INVALID_REQUEST_BODY(
        "INVALID_REQUEST_BODY",
        "Invalid request"
    ),

    INTERNAL_ERROR(
        "INTERNAL_ERROR",
        "Internal error"
    ),
    
    SERVICE_UNAVAILABLE(
        "SERVICE_UNAVAILABLE",
        "Service temporarily unavailable from BE"
    );

    private final String code;
    private final String message;

    private CommonErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }

}
