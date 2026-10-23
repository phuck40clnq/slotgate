package com.example.gateway.error;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.gateway.error.mapper.ErrorMapper;

@Component
public class GatewayErrorResolver {
    
    private final List<ErrorMapper> mappers;

    public GatewayErrorResolver(List<ErrorMapper> mappers) {
        this.mappers = mappers;
    }

    public GatewayErrorCode resolve(Throwable ex) {
        for (ErrorMapper mapper : mappers) {
            if (mapper.canHandle(ex)) {
                return mapper.map(ex);
            }
        }
        // Fallback
        return GatewayErrorCode.INTERNAL_ERROR;
    }

}
