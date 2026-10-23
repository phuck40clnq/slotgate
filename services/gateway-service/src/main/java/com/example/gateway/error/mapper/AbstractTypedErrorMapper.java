package com.example.gateway.error.mapper;

import com.example.gateway.error.GatewayErrorCode;

public abstract class AbstractTypedErrorMapper<T extends Throwable>
        extends AbstractErrorMapper {
    
    private final Class<T> type;

    protected AbstractTypedErrorMapper(Class<T> type) {
        this.type = type;
    }

    @Override
    public boolean canHandle(Throwable ex) {
        return this.type.isInstance(ex);
    }

    @Override
    protected final GatewayErrorCode doMap(Throwable ex) {
        return this.mapTyped(type.cast(ex));
    }

    protected abstract GatewayErrorCode mapTyped(T ex);
    
}
