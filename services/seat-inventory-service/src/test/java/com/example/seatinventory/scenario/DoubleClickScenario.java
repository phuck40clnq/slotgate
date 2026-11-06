package com.example.seatinventory.scenario;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;

import com.example.seatinventory.application.dto.OpenSectionCommand;
import com.example.seatinventory.application.dto.ReserveSeatCommand;
import com.example.seatinventory.application.port.in.OpenSectionUseCase;
import com.example.seatinventory.application.port.in.ReserveSeatUseCase;
import com.example.seatinventory.support.ParallelCallers;
import com.example.seatinventory.support.PostgresIntegrationTest;

/**
 * PRD failure scenario "a student clicks twice": all requests of one student for one section must end in
 * one hold, whether they arrive together or as a retry after a lost response.
 * Not part of the build. Run it on demand: {@code ./mvnw verify -Dit.test=DoubleClickScenario}
 */
class DoubleClickScenario extends PostgresIntegrationTest {

    private static final int SEATS = 10;
    private static final UUID STUDENT_ID = new UUID(0, 1);

    @Autowired
    private OpenSectionUseCase openSection;

    @Autowired
    private ReserveSeatUseCase reserveSeat;

    @Autowired
    private JdbcClient jdbcClient;

    @Test
    void retryAfterALostResponseKeepsOneHold() {
        UUID sectionId = openSection.openSection(new OpenSectionCommand(SEATS)).sectionId();
        ReserveSeatCommand command = new ReserveSeatCommand(sectionId, STUDENT_ID);
        Set<UUID> holdIds = ConcurrentHashMap.newKeySet();

        holdIds.add(reserveSeat.reserveSeat(command).holdId());
        holdIds.add(reserveSeat.reserveSeat(command).holdId());

        report("sequential retry", sectionId, holdIds);
        assertOneHold(sectionId, holdIds);
    }

    @ParameterizedTest(name = "{0} parallel requests from one student")
    @ValueSource(ints = {2, 20})
    void parallelRequestsKeepOneHold(int requests) {
        UUID sectionId = openSection.openSection(new OpenSectionCommand(SEATS)).sectionId();
        ReserveSeatCommand command = new ReserveSeatCommand(sectionId, STUDENT_ID);
        Set<UUID> holdIds = ConcurrentHashMap.newKeySet();

        ParallelCallers.Outcome outcome = ParallelCallers.run(requests, callerIndex ->
            holdIds.add(reserveSeat.reserveSeat(command).holdId()));

        report("%d parallel requests, accepted=%d rejected=%d p99=%dms".formatted(
            requests, outcome.accepted(), outcome.rejected(), outcome.latencyPercentile(99)), sectionId, holdIds);
        assertOneHold(sectionId, holdIds);
    }

    private void report(String label, UUID sectionId, Set<UUID> holdIds) {
        System.out.printf("SCENARIO %s: seats=%d heldSeats=%d reservedSeats=%d distinctHoldIds=%d%n",
            label, SEATS, seatHoldsOf(sectionId), reservedSeatsOf(sectionId), holdIds.size());
    }

    private void assertOneHold(UUID sectionId, Set<UUID> holdIds) {
        assertThat(seatHoldsOf(sectionId)).isEqualTo(1);
        assertThat(reservedSeatsOf(sectionId)).isEqualTo(1);
        assertThat(holdIds).hasSize(1);
    }

    private int seatHoldsOf(UUID sectionId) {
        return jdbcClient.sql("SELECT count(*) FROM seat_hold WHERE section_id = :id")
            .param("id", sectionId)
            .query(Integer.class)
            .single();
    }

    private int reservedSeatsOf(UUID sectionId) {
        return jdbcClient.sql("SELECT reserved_seats FROM section WHERE id = :id")
            .param("id", sectionId)
            .query(Integer.class)
            .single();
    }
}
