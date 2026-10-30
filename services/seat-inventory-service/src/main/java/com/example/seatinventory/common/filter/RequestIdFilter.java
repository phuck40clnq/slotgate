package com.example.seatinventory.common.filter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Puts the request ID into the MDC, so that every log line and error response carries it.
 */
@Component
@Slf4j
public class RequestIdFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String TRACE_ID_KEY = "traceId";
    private static final String CORRELATION_ID_KEY = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestId = Optional.ofNullable(request.getHeader(REQUEST_ID_HEADER))
            .orElseGet(() -> UUID.randomUUID().toString());
        MDC.put(TRACE_ID_KEY, requestId);
        MDC.put(CORRELATION_ID_KEY, requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);
        try {
            chain.doFilter(request, response);
        } finally {
            log.info("Completed request: method={}, path={}, status={}",
                request.getMethod(), request.getRequestURI(), response.getStatus());
            MDC.remove(TRACE_ID_KEY);
            MDC.remove(CORRELATION_ID_KEY);
        }
    }
}
