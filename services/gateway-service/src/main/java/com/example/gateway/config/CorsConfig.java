package com.example.gateway.config;

import java.util.List;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource(CorsProperties corsProperties) {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(corsProperties.allowedOrigins());

        config.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type"
        ));

        // A page reads Date to learn how far its clock is from the service's, for example to count
        // down a hold. A browser hides every header outside its safelist, and Date is outside it.
        config.setExposedHeaders(List.of(HttpHeaders.DATE));

        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = 
                new UrlBasedCorsConfigurationSource();
        
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
