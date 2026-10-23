package com.example.gateway.error;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Component
public class ErrorResponseWriter {
    
    private final ObjectMapper objectMapper;

    public ErrorResponseWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("null")
    // TODO: SuppressWarnings("null")
    public Mono<Void> write(
        ServerWebExchange exchange,
        HttpStatus status,
        String code,
        String message
    ) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String traceId = Optional.ofNullable(
            exchange.getRequest().getHeaders().getFirst("X-Trace-Id")
        ).orElse("N/A");

        ErrorResponse errorResponse = new ErrorResponse(
            code,
            message,
            traceId
        );

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            var buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return Mono.error(e);
        }

    }

}
