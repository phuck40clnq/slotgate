package com.example.seatinventory.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;

import com.example.seatinventory.application.dto.OpenSectionCommand;
import com.example.seatinventory.application.dto.ReserveSeatCommand;
import com.example.seatinventory.application.port.in.OpenSectionUseCase;
import com.example.seatinventory.application.port.in.ReserveSeatUseCase;
import com.example.seatinventory.support.ParallelCallers;
import com.example.seatinventory.support.PostgresIntegrationTest;

class ReserveSeatConcurrencyIT extends PostgresIntegrationTest {

    @Autowired
    private OpenSectionUseCase openSection;

    @Autowired
    private ReserveSeatUseCase reserveSeat;

    @Autowired
    private JdbcClient jdbcClient;

    @ParameterizedTest(name = "{0} seats, {1} parallel callers")
    @CsvSource({"1, 2", "1, 200", "10, 200", "200, 200"})
    void neverHoldsMoreSeatsThanCapacity(int capacity, int callers) {
        UUID sectionId = openSection.openSection(new OpenSectionCommand(capacity)).sectionId();

        ParallelCallers.Outcome outcome = ParallelCallers.run(callers, callerIndex ->
            reserveSeat.reserveSeat(new ReserveSeatCommand(sectionId, new UUID(0, callerIndex))));

        assertThat(outcome.accepted()).isEqualTo(capacity);
        assertThat(outcome.rejected()).isEqualTo(callers - capacity);
        assertThat(seatHoldsOf(sectionId)).isEqualTo(capacity);
        assertThat(reservedSeatsOf(sectionId)).isEqualTo(capacity);
    }

    @ParameterizedTest(name = "{0} parallel requests from one student")
    @ValueSource(ints = {2, 200})
    void holdsOneSeatForParallelRequestsOfOneStudent(int requests) {
        UUID sectionId = openSection.openSection(new OpenSectionCommand(10)).sectionId();
        ReserveSeatCommand command = new ReserveSeatCommand(sectionId, new UUID(0, 1));
        Set<UUID> holdIds = ConcurrentHashMap.newKeySet();

        ParallelCallers.Outcome outcome = ParallelCallers.run(requests, callerIndex ->
            holdIds.add(reserveSeat.reserveSeat(command).holdId()));

        assertThat(outcome.accepted()).isEqualTo(requests);
        assertThat(holdIds).hasSize(1);
        assertThat(seatHoldsOf(sectionId)).isEqualTo(1);
        assertThat(reservedSeatsOf(sectionId)).isEqualTo(1);
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
