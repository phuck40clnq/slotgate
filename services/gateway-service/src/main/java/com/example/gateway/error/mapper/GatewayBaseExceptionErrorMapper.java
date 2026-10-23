package com.example.gateway.error.mapper;

import org.springframework.stereotype.Component;

import com.example.gateway.error.GatewayBaseException;
import com.example.gateway.error.GatewayErrorCode;

@Component
public class GatewayBaseExceptionErrorMapper
        extends AbstractTypedErrorMapper<GatewayBaseException> {

    public GatewayBaseExceptionErrorMapper() {
        super(GatewayBaseException.class);
    }

    @Override
    protected GatewayErrorCode mapTyped(GatewayBaseException ex) {
        return ex.getErrorCode();
    }
    
}
