package com.example.test.common.filter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor
class RequestIdFilterIT {

    private static final String ME = "/api/v1/test/me";
    private static final String REQUEST_ID = "request-of-the-filter-test";

    private final TestRestTemplate rest;

    @Test
    void returnsTheRequestIdThatTheCallerSent() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", REQUEST_ID);

        ResponseEntity<String> response = rest.exchange(ME, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertThat(response.getHeaders().getFirst("X-Request-Id")).isEqualTo(REQUEST_ID);
    }

    @Test
    void createsAUuidWhenTheCallerSendsNoRequestId() {
        ResponseEntity<String> response = rest.getForEntity(ME, String.class);

        assertThat(response.getHeaders().getFirst("X-Request-Id")).satisfies(UUID::fromString);
    }
}
