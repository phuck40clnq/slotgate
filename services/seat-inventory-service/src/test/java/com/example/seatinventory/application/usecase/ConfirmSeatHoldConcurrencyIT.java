package com.example.seatinventory.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;

import com.example.seatinventory.application.dto.ConfirmSeatHoldCommand;
import com.example.seatinventory.application.dto.OpenSectionCommand;
import com.example.seatinventory.application.dto.ReserveSeatCommand;
import com.example.seatinventory.application.port.in.ConfirmSeatHoldUseCase;
import com.example.seatinventory.application.port.in.OpenSectionUseCase;
import com.example.seatinventory.application.port.in.ReserveSeatUseCase;
import com.example.seatinventory.support.ParallelCallers;
import com.example.seatinventory.support.PostgresIntegrationTest;

class ConfirmSeatHoldConcurrencyIT extends PostgresIntegrationTest {

    private static final UUID STUDENT_ID = new UUID(0, 1);

    @Autowired
    private OpenSectionUseCase openSection;

    @Autowired
    private ReserveSeatUseCase reserveSeat;

    @Autowired
    private ConfirmSeatHoldUseCase confirmSeatHold;

    @Autowired
    private JdbcClient jdbcClient;

    @ParameterizedTest(name = "{0} parallel confirmations of one hold")
    @ValueSource(ints = {2, 200})
    void confirmsEveryParallelRequestForOneHold(int requests) {
        UUID sectionId = openSection.openSection(new OpenSectionCommand(1)).sectionId();
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
