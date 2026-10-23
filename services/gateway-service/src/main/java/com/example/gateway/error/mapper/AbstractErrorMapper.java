package com.example.gateway.error.mapper;

import com.example.gateway.error.GatewayErrorCode;

public non-sealed abstract class AbstractErrorMapper
        implements ErrorMapper {
    
    @Override
    public final GatewayErrorCode map(Throwable ex) {
        if (!canHandle(ex)) {
            throw new IllegalStateException(
                getClass().getSimpleName()
                    + " cannot handle exception", ex
            );
        }
        return doMap(ex);
    }
    
    @Override
    public abstract boolean canHandle(Throwable ex);

    protected abstract GatewayErrorCode doMap(Throwable ex);
}
