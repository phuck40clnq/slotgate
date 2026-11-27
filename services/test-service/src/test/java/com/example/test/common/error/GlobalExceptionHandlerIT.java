package com.example.test.common.error;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor
class GlobalExceptionHandlerIT {

    private static final String REQUEST_ID = "request-of-the-handler-test";

    private final TestRestTemplate rest;

    @Test
    void carriesTheRequestIdAsTraceId() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", REQUEST_ID);

        ErrorResponse error = rest.exchange(
            "/api/v1/test/errors/not-found", HttpMethod.GET, new HttpEntity<>(headers), ErrorResponse.class)
            .getBody();

        assertThat(error.traceId()).isEqualTo(REQUEST_ID);
    }

    @Test
    void keepsTheDetailsOfAnUnexpectedFailureOutOfTheAnswer() {
        ErrorResponse error = rest.getForObject("/api/v1/test/errors/internal", ErrorResponse.class);

        assertThat(error.message()).isEqualTo("Internal error");
    }

    @ParameterizedTest(name = "{0} {1}")
    @CsvSource({
        "GET,  /api/v1/test/no-such-resource, 404, RESOURCE_NOT_FOUND",
        "POST, /api/v1/test/me,               405, METHOD_NOT_ALLOWED"
    })
    void answersARequestThatNoOperationServesAsAClientError(HttpMethod method, String path, int status, String code) {
        ResponseEntity<ErrorResponse> response = rest.exchange(path, method, HttpEntity.EMPTY, ErrorResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(status);
        assertThat(response.getBody().code()).isEqualTo(code);
    }
}
