package com.example.gateway.filter.identity;

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

import com.example.gateway.common.constants.HeaderConstants;
import com.example.gateway.support.DownstreamEcho;
import com.example.gateway.support.StudentFixtures;
import com.example.gateway.support.TokenIssuer;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor
class IdentityPropagationFilterIT {

    private static final String FORGED_STUDENT_ID = "00000000-0000-0000-0000-00000000000f";

    private static final DownstreamEcho DOWNSTREAM = DownstreamEcho.start(HeaderConstants.X_INTERNAL_User_Id);
    private static final TokenIssuer ISSUER = TokenIssuer.start();

    private final TestRestTemplate rest;

    @DynamicPropertySource
    static void routeServicesToDownstreamEcho(DynamicPropertyRegistry registry) {
        registry.add("OAUTH_JWK_SET_URI", ISSUER::keySetUrl);
        registry.add("spring.cloud.discovery.client.simple.instances.test-service[0].uri", DOWNSTREAM::url);
        registry.add("spring.cloud.discovery.client.simple.instances.user-service[0].uri", DOWNSTREAM::url);
    }

    @AfterAll
    static void stopServers() {
        DOWNSTREAM.stop();
        ISSUER.stop();
    }

    @ParameterizedTest(name = "header {0}")
    @ValueSource(strings = {"X-Internal-User-Id", "x-internal-user-id"})
    void dropsIdentityThatClientSendsOnRouteWithoutTokenCheck(String headerName) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(headerName, FORGED_STUDENT_ID);

        ResponseEntity<String> forwarded = get("/api/test/echo", headers);

        assertThat(forwarded.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(forwarded.getBody()).isNull();
    }

    @Test
    void replacesIdentityThatClientSendsWithTokenSubject() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(ISSUER.issueFor(StudentFixtures.STUDENT_ID));
        headers.set(HeaderConstants.X_INTERNAL_User_Id, FORGED_STUDENT_ID);

        ResponseEntity<String> forwarded = get("/api/user/echo", headers);

        assertThat(forwarded.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(forwarded.getBody()).isEqualTo(StudentFixtures.STUDENT_ID);
    }

    private ResponseEntity<String> get(String path, HttpHeaders headers) {
        return rest.exchange(path, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }
}
