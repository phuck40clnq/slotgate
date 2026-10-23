package com.example.gateway.error;

public class GatewayBaseException
        extends RuntimeException {
    
    private final GatewayErrorCode errorCode;

    public GatewayBaseException(GatewayErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    public GatewayBaseException(
        GatewayErrorCode errorCode,
        Throwable cause
    ) {
        super(errorCode.message(), cause);
        this.errorCode = errorCode;
    }

    public GatewayErrorCode getErrorCode() {
        return errorCode;
    }

}
