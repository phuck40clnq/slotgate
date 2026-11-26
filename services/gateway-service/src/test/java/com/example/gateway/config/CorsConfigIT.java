package com.example.gateway.config;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// A preflight sends no token, so the decoder never fetches this key set.
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {
        "OAUTH_JWK_SET_URI=http://localhost/unused-jwks",
        "CORS_ALLOWED_ORIGINS=http://school-a.example.test, https://school-b.example.test:8443"
    }
)
@RequiredArgsConstructor
class CorsConfigIT {

    private final TestRestTemplate rest;

    @ParameterizedTest(name = "origin {0}")
    @ValueSource(strings = {"http://school-a.example.test", "https://school-b.example.test:8443"})
    void allowsPreflightFromConfiguredOrigin(String origin) {
        ResponseEntity<Void> response = preflightFrom(origin);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getAccessControlAllowOrigin()).isEqualTo(origin);
    }

    @ParameterizedTest(name = "origin {0}")
    @ValueSource(strings = {"http://localhost:3000", "http://school-c.example.test"})
    void rejectsPreflightFromOriginNotConfigured(String origin) {
        ResponseEntity<Void> response = preflightFrom(origin);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getHeaders().getAccessControlAllowOrigin()).isNull();
    }

    private ResponseEntity<Void> preflightFrom(String origin) {
        HttpHeaders headers = new HttpHeaders();
        headers.setOrigin(origin);
        headers.setAccessControlRequestMethod(HttpMethod.GET);
        return rest.exchange(
            "/api/seat-inventory/sections", HttpMethod.OPTIONS, new HttpEntity<>(headers), Void.class
        );
    }
}
