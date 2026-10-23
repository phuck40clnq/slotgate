package com.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

import com.example.gateway.error.ErrorResponseWriter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
        ServerHttpSecurity http,
        CorsConfigurationSource corsConfigurationSource,
        ErrorResponseWriter errorResponseWriter
    ) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/api/auth/**").permitAll()
                .pathMatchers("/public/**").permitAll() // TODO: [LATER] Fix security config (remove public api)
                .pathMatchers("/api/test/**").permitAll()
                .pathMatchers("/api/**").authenticated() // Delete this line
                .anyExchange().authenticated()
            )

            .exceptionHandling(ex -> ex
                .accessDeniedHandler((exchange, denied) -> 
                    errorResponseWriter.write(
                        exchange,
                        HttpStatus.FORBIDDEN,
                        "GATEWAY_403",
                        "You do not have permission"
                    )
                )
            )
            
            .oauth2ResourceServer(oauth2 ->
                oauth2
                    .authenticationEntryPoint((exchange, ex) ->
                        errorResponseWriter.write(
                            exchange,
                            HttpStatus.UNAUTHORIZED,
                            "GATEWAY_401",
                            "Missing or invalid token"
                        )
                    )
                    .jwt(Customizer.withDefaults())
            )

            .build();
    }
}
