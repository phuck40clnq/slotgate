package com.example.gateway.config;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

// The origins whose pages may call the gateway from a browser, read from CORS_ALLOWED_ORIGINS.
@ConfigurationProperties(prefix = "gateway.cors")
@Validated
public record CorsProperties(List<String> allowedOrigins) {

    // A scheme, a host and an optional port, as a browser sends them in Origin. No wildcard, because the
    // gateway allows credentials, and a browser refuses a wildcard origin on a request with credentials.
    private static final Pattern ORIGIN = Pattern.compile("https?://[^/*\\s]+");

    public CorsProperties {
        Objects.requireNonNull(allowedOrigins, "allowedOrigins");
        allowedOrigins = List.copyOf(allowedOrigins);
        if (allowedOrigins.isEmpty()) {
            throw new IllegalArgumentException("CORS_ALLOWED_ORIGINS must name at least one origin");
        }
        for (String origin : allowedOrigins) {
            if (!ORIGIN.matcher(origin).matches()) {
                throw new IllegalArgumentException("CORS origin must be scheme://host[:port]: '" + origin + "'");
            }
        }
    }
}
