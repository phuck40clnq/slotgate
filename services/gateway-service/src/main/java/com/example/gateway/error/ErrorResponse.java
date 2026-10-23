package com.example.gateway.error;

public record ErrorResponse(
    String code,
    String message,
    String traceId
) {}
