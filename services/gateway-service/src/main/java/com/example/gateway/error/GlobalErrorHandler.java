package com.example.gateway.error;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Component
// Order defines the priority order (-1 is default)
// Higher priority than DefaultErrorWebExceptionHandler (-1)
@Order(-2)
public class GlobalErrorHandler implements ErrorWebExceptionHandler {
    
    private static final Logger log =
        LoggerFactory.getLogger(GlobalErrorHandler.class);

    private final ObjectMapper objectMapper;
    private final GatewayErrorResolver resolver;

    public GlobalErrorHandler(
        ObjectMapper objectMapper,
        GatewayErrorResolver resolver
    ) {
        this.objectMapper = objectMapper;
        this.resolver = resolver;
    }

    /** TODO: Temporary add to lack of warnings */
    @SuppressWarnings("null")
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        GatewayErrorCode errorCode = resolver.resolve(ex);

        response.setStatusCode(errorCode.status());
        response.getHeaders().setContentType(
            MediaType.APPLICATION_JSON
        );

        String traceId = Optional.ofNullable(
            exchange.getRequest().getHeaders().getFirst("X-Trace-Id")
        ).orElse("N/A");

        ErrorResponse body = new ErrorResponse(
            errorCode.code(),
            errorCode.message(),
            traceId
        );

        log.error(
            "Gateway error: code={}, traceId={}, message={}",
            errorCode,
            traceId,
            ex.getMessage(),
            ex
        );

        byte[] bytes = serialize(body);

        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer));
    }

    private byte[] serialize(Object body) {
        try {
            return objectMapper.writeValueAsBytes(body);
        } catch (Exception e) {
            log.error("Failed to serialize error response", e);
            return "{\"code\":\"GW_500\",\"message\":\"Internal error\"}".getBytes(StandardCharsets.UTF_8);
        }
    }

}
