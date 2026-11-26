package com.example.seatinventory.adapter.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;

import com.example.seatinventory.adapter.web.dto.StudentHoldResponse;
import com.example.seatinventory.adapter.web.dto.StudentHoldsResponse;
import com.example.seatinventory.application.dto.ConfirmSeatHoldCommand;
import com.example.seatinventory.application.dto.OpenSectionCommand;
import com.example.seatinventory.application.dto.ReserveSeatCommand;
import com.example.seatinventory.application.dto.SeatHoldResult;
import com.example.seatinventory.application.port.in.ConfirmSeatHoldUseCase;
import com.example.seatinventory.application.port.in.OpenSectionUseCase;
import com.example.seatinventory.application.port.in.ReserveSeatUseCase;
import com.example.seatinventory.common.error.ErrorResponse;
import com.example.seatinventory.support.PostgresIntegrationTest;

@RequiredArgsConstructor
class StudentHoldApiIT extends PostgresIntegrationTest {

    private static final String STUDENT_HOLDS = "/api/v1/seat-inventory/me/holds";

    private final TestRestTemplate rest;

    private final OpenSectionUseCase openSection;

    private final ReserveSeatUseCase reserveSeat;

    private final ConfirmSeatHoldUseCase confirmSeatHold;

    private final JdbcClient jdbcClient;

    @Test
    void listsTheHeldAndConfirmedSeatsOfTheCallingStudentByCode() {
        UUID studentId = UUID.randomUUID();
        UUID heldSectionId = open("ME-B", "Held course");
        UUID confirmedSectionId = open("ME-A", "Confirmed course");
        SeatHoldResult held = reserveSeat.reserveSeat(new ReserveSeatCommand(heldSectionId, studentId));
        SeatHoldResult confirmed = reserveSeat.reserveSeat(new ReserveSeatCommand(confirmedSectionId, studentId));
        confirmSeatHold.confirmSeatHold(new ConfirmSeatHoldCommand(confirmedSectionId, confirmed.holdId(), studentId));

        ResponseEntity<StudentHoldsResponse> response = listHoldsOf(studentId, StudentHoldsResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().holds()).containsExactly(
            new StudentHoldResponse(confirmed.holdId(), confirmedSectionId, "ME-A", "Confirmed course",
                confirmed.heldAt(), confirmed.expiresAt(), "CONFIRMED"),
            new StudentHoldResponse(held.holdId(), heldSectionId, "ME-B", "Held course", held.heldAt(),
                held.expiresAt(), "HELD"));
    }

    @Test
    void leavesOutAHoldWhoseTimeIsUp() {
        UUID studentId = UUID.randomUUID();
        SeatHoldResult held = reserveSeat.reserveSeat(new ReserveSeatCommand(open("ME-UP", "Course"), studentId));
        endHoldPeriod(held.holdId());

        StudentHoldsResponse response = listHoldsOf(studentId, StudentHoldsResponse.class).getBody();

        assertThat(response.holds()).isEmpty();
    }

    @Test
    void refusesTheListWithoutStudentIdentity() {
        ResponseEntity<ErrorResponse> response = rest.getForEntity(STUDENT_HOLDS, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().code()).isEqualTo("INVALID_REQUEST");
    }

    private UUID open(String code, String title) {
        return openSection.openSection(new OpenSectionCommand(code, title, 5)).sectionId();
    }

    private <T> ResponseEntity<T> listHoldsOf(UUID studentId, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-User-Id", studentId.toString());
        return rest.exchange(STUDENT_HOLDS, HttpMethod.GET, new HttpEntity<>(headers), responseType);
    }

    private void endHoldPeriod(UUID holdId) {
        jdbcClient.sql("UPDATE seat_hold SET expires_at = held_at WHERE id = :id")
            .param("id", holdId)
            .update();
    }
}
