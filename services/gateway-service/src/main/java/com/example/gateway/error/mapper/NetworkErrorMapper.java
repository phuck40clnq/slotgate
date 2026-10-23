package com.example.gateway.error.mapper;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.gateway.error.GatewayErrorCode;
import com.example.gateway.error.utils.ExceptionUtils;

import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.ReadTimeoutException;

@Component
public class NetworkErrorMapper
        extends AbstractErrorMapper {

    @Override
    public boolean canHandle(Throwable ex) {
        return this.isTimeout(ex)
            || this.isBadGateway(ex);
    }

    @Override
    public GatewayErrorCode doMap(Throwable ex) {
        return isTimeout(ex)
            ? GatewayErrorCode.TIMEOUT
            : GatewayErrorCode.BAD_GATEWAY;
    }

    private boolean isTimeout(Throwable ex) {
        return ExceptionUtils.hasCause(ex, TimeoutException.class)
            || ExceptionUtils.hasCause(ex, ReadTimeoutException.class)
            || ExceptionUtils.hasCause(ex, ConnectTimeoutException.class);
    }

    private boolean isBadGateway(Throwable ex) {
        return ExceptionUtils.hasCause(ex, WebClientResponseException.BadGateway.class)
            || ExceptionUtils.hasCause(ex, ConnectException.class);
    }
    
}
