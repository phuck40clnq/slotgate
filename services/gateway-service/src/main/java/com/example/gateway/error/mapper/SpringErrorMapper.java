package com.example.gateway.error.mapper;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.example.gateway.error.GatewayErrorCode;
import com.example.gateway.error.utils.ExceptionUtils;

@Component
public class SpringErrorMapper
        extends AbstractErrorMapper {

    @Override
    public boolean canHandle(Throwable ex) {
        return ExceptionUtils.hasCause(ex, ResponseStatusException.class);
    }

    @Override
    protected GatewayErrorCode doMap(Throwable ex) {
        ResponseStatusException rse =
            ExceptionUtils.findCause(ex, ResponseStatusException.class);

        return GatewayErrorCode.fromHttpStatusCode(rse.getStatusCode());
    }

}
