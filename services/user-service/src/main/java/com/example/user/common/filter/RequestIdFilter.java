package com.example.user.common.filter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RequestIdFilter implements Filter {

    /** Header name for passing request ID in HTTP requests/responses */
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    /** MDC key for the trace ID used for logging */
    private static final String TRACE_ID_KEY = "traceId";

    /** MDC key for the correlation ID (can be used across services) */
    private static final String CORRELATION_ID_KEY = "correlationId";

    /**
     * Assigns a trace ID to the incoming HTTP request and stores it in MDC for logging.
     * Also sets the trace ID in the HTTP response header.
     *
     * @param req   the incoming ServletRequest
     * @param res   the outgoing ServletResponse
     * @param chain the filter chain
     * @throws IOException      if an I/O error occurs during processing
     * @throws ServletException if a servlet error occurs during processing
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Get request ID from header, or generate a new UUID if not present
        String requestId = Optional
                .ofNullable(request.getHeader(REQUEST_ID_HEADER))
                .orElse(UUID.randomUUID().toString());

        long startTime = System.currentTimeMillis(); // track duration

        try {
            // Put requestId into MDC so logback JSON encoder can pick it up
            MDC.put(TRACE_ID_KEY, requestId);
            MDC.put(CORRELATION_ID_KEY, requestId);

            // Set the request ID in the response header so client knows it
            response.setHeader(REQUEST_ID_HEADER, requestId);

            // 🔹 Log incoming request
            log.info("Incoming request [{}] {} {}", 
                    requestId, 
                    request.getMethod(), 
                    request.getRequestURI());

            // Continue with the filter chain
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // 🔹 Log completed request
            log.info("Completed request [{}] {} {} - status: {} - duration: {}ms",
                    requestId,
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration
            );

            // Clean up MDC to avoid leaking data to other requests
            MDC.remove(TRACE_ID_KEY);
            MDC.remove(CORRELATION_ID_KEY);
        }
    }
}
