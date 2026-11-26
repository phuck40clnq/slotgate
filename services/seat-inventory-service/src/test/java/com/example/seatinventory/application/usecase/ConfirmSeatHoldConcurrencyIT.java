package com.example.seatinventory.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.jdbc.core.simple.JdbcClient;

import com.example.seatinventory.application.dto.ConfirmSeatHoldCommand;
import com.example.seatinventory.application.dto.ReserveSeatCommand;
import com.example.seatinventory.application.port.in.ConfirmSeatHoldUseCase;
import com.example.seatinventory.application.port.in.OpenSectionUseCase;
import com.example.seatinventory.application.port.in.ReserveSeatUseCase;
import com.example.seatinventory.support.ParallelCallers;
import com.example.seatinventory.support.PostgresIntegrationTest;
import com.example.seatinventory.support.SectionFixtures;

@RequiredArgsConstructor
class ConfirmSeatHoldConcurrencyIT extends PostgresIntegrationTest {

    private static final UUID STUDENT_ID = new UUID(0, 1);

    private final OpenSectionUseCase openSection;

    private final ReserveSeatUseCase reserveSeat;

    private final ConfirmSeatHoldUseCase confirmSeatHold;

    private final JdbcClient jdbcClient;

    @ParameterizedTest(name = "{0} parallel confirmations of one hold")
    @ValueSource(ints = {2, 200})
    void confirmsEveryParallelRequestForOneHold(int requests) {
        UUID sectionId = openSection.openSection(SectionFixtures.openSectionCommand(1)).sectionId();
        UUID holdId = reserveSeat.reserveSeat(new ReserveSeatCommand(sectionId, STUDENT_ID)).holdId();
        ConfirmSeatHoldCommand command = new ConfirmSeatHoldCommand(sectionId, holdId, STUDENT_ID);
        Set<String> statuses = ConcurrentHashMap.newKeySet();

        ParallelCallers.Outcome outcome = ParallelCallers.run(requests, callerIndex ->
            statuses.add(confirmSeatHold.confirmSeatHold(command).status()));

        assertThat(outcome.accepted()).isEqualTo(requests);
        assertThat(statuses).containsExactly("CONFIRMED");
        assertThat(statusOf(holdId)).isEqualTo("CONFIRMED");
    }

    private String statusOf(UUID holdId) {
        return jdbcClient.sql("SELECT status FROM seat_hold WHERE id = :id")
            .param("id", holdId)
            .query(String.class)
            .single();
    }
}
