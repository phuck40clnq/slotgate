package com.example.gateway.error.mapper;

import com.example.gateway.error.GatewayErrorCode;

public sealed interface ErrorMapper
        permits AbstractErrorMapper {

    boolean canHandle(Throwable ex);

    GatewayErrorCode map(Throwable ex);

}
