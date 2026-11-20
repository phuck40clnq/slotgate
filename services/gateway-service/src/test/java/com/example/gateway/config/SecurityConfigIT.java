package com.example.gateway.config;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// The health check sends no token, so the decoder never fetches this key set.
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = "OAUTH_JWK_SET_URI=http://localhost/unused-jwks"
)
@RequiredArgsConstructor
class SecurityConfigIT {

    private final TestRestTemplate rest;

    @Test
    void servesHealthWithoutToken() {
        ResponseEntity<String> response = rest.getForEntity("/actuator/health", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("{\"status\":\"UP\"}");
    }
}
