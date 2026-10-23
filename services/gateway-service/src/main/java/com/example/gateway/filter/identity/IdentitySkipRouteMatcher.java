package com.example.gateway.filter.identity;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.server.PathContainer;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

@ConfigurationProperties(prefix = "gateway.identity")
public class IdentitySkipRouteMatcher {
    
    private List<String> skip;
    private final PathPatternParser parser = new PathPatternParser();
    private List<PathPattern> patterns;

    public boolean shouldSkip(ServerWebExchange exchange) {
        String path = exchange.getRequest()
                .getPath()
                .value();
        
        return patterns.stream().anyMatch(
            p -> p.matches(PathContainer.parsePath(path))
        );
    }

    // Helper for Spring
    public void setSkip(List<String> skip) {
        this.skip = skip;
        if (skip == null) {
            this.patterns = List.of();
        } else {
            this.patterns = skip
                    .stream()
                    .map(s -> {
                        if (s == null) {
                            throw new IllegalStateException("Public route pattern must not be null");
                        }
                        return parser.parse(s);
                    })
                    .toList();
        }
    }

    public List<String> getSkip() {
        return this.skip;
    }
}
