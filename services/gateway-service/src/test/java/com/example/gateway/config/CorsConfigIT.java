package com.example.gateway.config;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.example.gateway.support.DownstreamEcho;

// These requests send no token, so the decoder never fetches this key set.
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {
        "OAUTH_JWK_SET_URI=http://localhost/unused-jwks",
        "CORS_ALLOWED_ORIGINS=http://school-a.example.test, https://school-b.example.test:8443"
    }
)
@RequiredArgsConstructor
class CorsConfigIT {

    private static final String SCHOOL_A = "http://school-a.example.test";

    private static final DownstreamEcho DOWNSTREAM = DownstreamEcho.start(HttpHeaders.ORIGIN);

    private final TestRestTemplate rest;

    @DynamicPropertySource
    static void routeTestServiceToDownstreamEcho(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.discovery.client.simple.instances.test-service[0].uri", DOWNSTREAM::url);
    }

    @AfterAll
    static void stopDownstream() {
        DOWNSTREAM.stop();
    }

    @ParameterizedTest(name = "origin {0}")
    @ValueSource(strings = {SCHOOL_A, "https://school-b.example.test:8443"})
    void allowsPreflightFromConfiguredOrigin(String origin) {
        ResponseEntity<Void> response = preflightFrom(origin);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getAccessControlAllowOrigin()).isEqualTo(origin);
    }

    // A page reads Date to learn how far its clock is from the service's, and a browser hides
    // that header from a page on another origin unless the response exposes it.
    @Test
    void exposesDateOfServiceToPageFromConfiguredOrigin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setOrigin(SCHOOL_A);

        ResponseEntity<String> forwarded = rest.exchange(
            "/api/test/echo", HttpMethod.GET, new HttpEntity<>(headers), String.class
        );

        assertThat(forwarded.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(forwarded.getHeaders().getAccessControlExposeHeaders()).containsExactly(HttpHeaders.DATE);
        assertThat(forwarded.getHeaders().getFirst(HttpHeaders.DATE)).isNotBlank();
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
