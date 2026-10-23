package com.example.gateway.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum GatewayErrorCode {

    BAD_REQUEST(
        "GATEWAY_400",
        "Bad request",
        HttpStatus.BAD_REQUEST
    ),

    UNAUTHORIZED(
        "GATEWAY_401",
        "Unauthorized",
        HttpStatus.UNAUTHORIZED
    ),
    
    FORBIDDEN(
        "GATEWAY_403",
        "Forbidden",
        HttpStatus.FORBIDDEN
    ),

    ROUTE_NOT_FOUND(
        "GATEWAY_404",
        "Route not found",
        HttpStatus.NOT_FOUND
    ),

    BAD_GATEWAY(
        "GATEWAY_502",
        "Bad gateway",
        HttpStatus.BAD_GATEWAY
    ),

    SERVICE_UNAVAILABLE(
        "GATEWAY_503",
        "Service temporarily unavailable",
        HttpStatus.SERVICE_UNAVAILABLE
    ),

    TIMEOUT(
        "GATEWAY_504",
        "Upstream timeout",
        HttpStatus.GATEWAY_TIMEOUT
    ),

    INTERNAL_ERROR(
        "GATEWAY_500",
        "Internal gateway error",
        HttpStatus.INTERNAL_SERVER_ERROR
    );
    
    private final String code;
    private final String message;
    private final HttpStatus status;

    GatewayErrorCode(
        String code,
        String message,
        HttpStatus status
    ) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String code() { return this.code; }
    public String message() { return this.message; }
    public HttpStatus status() { return this.status; }

    // Convenience constructor
    public static GatewayErrorCode fromHttpStatus(HttpStatus status) {
        if (status == null) {
            return INTERNAL_ERROR;
        }

        for (GatewayErrorCode code : values()) {
            if (code.status == status) {
                return code;
            }
        }
        return INTERNAL_ERROR;
    }

    public static GatewayErrorCode fromHttpStatusCode(HttpStatusCode statusCode) {
        if (statusCode == null) {
            return INTERNAL_ERROR;
        }
        
        return fromHttpStatus(
            HttpStatus.resolve(
                statusCode.value()
            )
        );
    }
}
