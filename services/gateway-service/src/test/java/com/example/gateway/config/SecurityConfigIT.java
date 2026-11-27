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

import com.example.gateway.common.constants.HeaderConstants;
import com.example.gateway.error.ErrorResponse;
import com.example.gateway.support.DownstreamEcho;
import com.example.gateway.support.StudentFixtures;
import com.example.gateway.support.TokenIssuer;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor
class SecurityConfigIT {

    private static final String SECTIONS_PATH = "/api/seat-inventory/sections";
    private static final String SEAT_INVENTORY_URI_PROPERTY =
        "spring.cloud.discovery.client.simple.instances.seat-inventory-service[0].uri";

    private static final DownstreamEcho SEAT_INVENTORY = DownstreamEcho.start(HeaderConstants.X_INTERNAL_User_Id);
    private static final TokenIssuer ISSUER = TokenIssuer.start();

    private final TestRestTemplate rest;

    @DynamicPropertySource
    static void routeSeatInventoryToDownstreamEcho(DynamicPropertyRegistry registry) {
        registry.add("OAUTH_JWK_SET_URI", ISSUER::keySetUrl);
        registry.add(SEAT_INVENTORY_URI_PROPERTY, SEAT_INVENTORY::url);
    }

    @AfterAll
    static void stopServers() {
        SEAT_INVENTORY.stop();
        ISSUER.stop();
    }

    @Test
    void servesHealthWithoutToken() {
        ResponseEntity<String> response = rest.getForEntity("/actuator/health", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("{\"status\":\"UP\"}");
    }

    @ParameterizedTest(name = "path {0}")
    @ValueSource(strings = {SECTIONS_PATH, SECTIONS_PATH + "/"})
    void refusesToOpenSectionForStudentWithToken(String path) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(ISSUER.issueFor(StudentFixtures.STUDENT_ID));

        ResponseEntity<ErrorResponse> response = rest.exchange(
            path, HttpMethod.POST, new HttpEntity<>(headers), ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().code()).isEqualTo("GATEWAY_403");
    }

    @Test
    void rejectsOpeningSectionWithoutTokenAsUnauthenticated() {
        ResponseEntity<ErrorResponse> response = rest.exchange(
            SECTIONS_PATH, HttpMethod.POST, HttpEntity.EMPTY, ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().code()).isEqualTo("GATEWAY_401");
    }

    @Test
    void forwardsSectionListToSeatInventory() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(ISSUER.issueFor(StudentFixtures.STUDENT_ID));

        ResponseEntity<String> forwarded = rest.exchange(
            SECTIONS_PATH, HttpMethod.GET, new HttpEntity<>(headers), String.class
        );

        assertThat(forwarded.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(forwarded.getBody()).isEqualTo(StudentFixtures.STUDENT_ID);
    }
}
