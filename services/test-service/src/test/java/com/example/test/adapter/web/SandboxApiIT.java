package com.example.test.adapter.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.test.adapter.web.dto.CallerResponse;
import com.example.test.common.error.ErrorResponse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor
class SandboxApiIT {

    private static final String ME = "/api/v1/test/me";
    private static final UUID CALLER_ID = UUID.fromString("00000000-0000-0000-0000-00000000000a");

    private final TestRestTemplate rest;

    @Test
    void returnsTheCallerIdThatTheHeaderCarries() {
        ResponseEntity<CallerResponse> response = getMe(CALLER_ID.toString(), CallerResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(new CallerResponse(CALLER_ID));
    }

    @Test
    void answersNoContentWhenNoCallerIdArrives() {
        ResponseEntity<String> response = rest.getForEntity(ME, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

    @ParameterizedTest(name = "caller ID \"{0}\"")
    @ValueSource(strings = {"not-a-uuid", ""})
    void refusesACallerIdThatIsNotAUuid(String callerId) {
        ResponseEntity<ErrorResponse> response = getMe(callerId, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().code()).isEqualTo("INVALID_REQUEST");
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource({
        "bad-request, 400, SIMULATED_BAD_REQUEST",
        "not-found,   404, SIMULATED_NOT_FOUND",
        "internal,    500, INTERNAL_ERROR"
    })
    void answersEverySimulatedErrorWithItsStatusAndCode(String error, int status, String code) {
        ResponseEntity<ErrorResponse> response =
            rest.getForEntity("/api/v1/test/errors/" + error, ErrorResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(status);
        assertThat(response.getBody().code()).isEqualTo(code);
    }

    private <T> ResponseEntity<T> getMe(String callerId, Class<T> type) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-User-Id", callerId);
        return rest.exchange(ME, HttpMethod.GET, new HttpEntity<>(headers), type);
    }
}
