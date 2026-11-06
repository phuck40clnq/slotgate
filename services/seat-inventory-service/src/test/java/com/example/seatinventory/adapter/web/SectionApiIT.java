package com.example.seatinventory.adapter.web;

import static com.example.seatinventory.support.SectionFixtures.STUDENT_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.seatinventory.adapter.web.dto.SeatHoldResponse;
import com.example.seatinventory.adapter.web.dto.SectionResponse;
import com.example.seatinventory.common.error.ErrorResponse;
import com.example.seatinventory.support.PostgresIntegrationTest;

class SectionApiIT extends PostgresIntegrationTest {

    private static final String SECTIONS = "/api/v1/seat-inventory/sections";
    private static final UUID OTHER_STUDENT_ID = UUID.fromString("00000000-0000-0000-0000-00000000000d");

    @Autowired
    private TestRestTemplate rest;

    @Test
    void opensSectionWithEverySeatLeft() {
        ResponseEntity<SectionResponse> response = open(40, SectionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().capacity()).isEqualTo(40);
        assertThat(response.getBody().seatsLeft()).isEqualTo(40);
    }

    @Test
    void holdsSeatForTheCallingStudent() {
        UUID sectionId = openSection(1);

        ResponseEntity<SeatHoldResponse> response = hold(sectionId, STUDENT_ID, SeatHoldResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().sectionId()).isEqualTo(sectionId);
        assertThat(response.getBody().studentId()).isEqualTo(STUDENT_ID);
    }

    @Test
    void returnsTheSameHoldWhenTheStudentAsksAgainForTheLastSeat() {
        UUID sectionId = openSection(1);
        SeatHoldResponse firstHold = hold(sectionId, STUDENT_ID, SeatHoldResponse.class).getBody();

        ResponseEntity<SeatHoldResponse> response = hold(sectionId, STUDENT_ID, SeatHoldResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(firstHold);
    }

    @Test
    void showsOneSeatLessAfterAHold() {
        UUID sectionId = openSection(2);
        hold(sectionId, STUDENT_ID, SeatHoldResponse.class);

        SectionResponse section = rest.getForObject(SECTIONS + "/" + sectionId, SectionResponse.class);

        assertThat(section.seatsLeft()).isEqualTo(1);
    }

    @Test
    void refusesHoldWhenSectionIsFull() {
        UUID sectionId = openSection(1);
        hold(sectionId, STUDENT_ID, SeatHoldResponse.class);

        ResponseEntity<ErrorResponse> response = hold(sectionId, OTHER_STUDENT_ID, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().code()).isEqualTo("SECTION_FULL");
    }

    @Test
    void refusesHoldOnUnknownSection() {
        ResponseEntity<ErrorResponse> response = hold(UUID.randomUUID(), STUDENT_ID, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().code()).isEqualTo("SECTION_NOT_FOUND");
    }

    @Test
    void refusesReadOfUnknownSection() {
        ResponseEntity<ErrorResponse> response =
            rest.getForEntity(SECTIONS + "/" + UUID.randomUUID(), ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().code()).isEqualTo("SECTION_NOT_FOUND");
    }

    @Test
    void refusesCapacityBelowOne() {
        ResponseEntity<ErrorResponse> response = open(0, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().code()).isEqualTo("INVALID_REQUEST");
    }

    @Test
    void refusesHoldWithoutStudentIdentity() {
        ResponseEntity<ErrorResponse> response = rest.postForEntity(
            SECTIONS + "/" + openSection(1) + "/holds", HttpEntity.EMPTY, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().code()).isEqualTo("INVALID_REQUEST");
    }

    @Test
    void refusesMalformedSectionId() {
        ResponseEntity<ErrorResponse> response =
            rest.getForEntity(SECTIONS + "/not-a-uuid", ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().code()).isEqualTo("INVALID_REQUEST");
    }

    @Test
    void carriesTheRequestIdInTheErrorResponse() {
        ResponseEntity<ErrorResponse> response = hold(UUID.randomUUID(), STUDENT_ID, ErrorResponse.class);

        assertThat(response.getBody().traceId())
            .isNotBlank()
            .isEqualTo(response.getHeaders().getFirst("X-Request-Id"));
    }

    private UUID openSection(int capacity) {
        return open(capacity, SectionResponse.class).getBody().sectionId();
    }

    private <T> ResponseEntity<T> open(int capacity, Class<T> responseType) {
        return rest.postForEntity(SECTIONS, Map.of("capacity", capacity), responseType);
    }

    private <T> ResponseEntity<T> hold(UUID sectionId, UUID studentId, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-User-Id", studentId.toString());
        return rest.postForEntity(SECTIONS + "/" + sectionId + "/holds", new HttpEntity<>(headers), responseType);
    }
}
