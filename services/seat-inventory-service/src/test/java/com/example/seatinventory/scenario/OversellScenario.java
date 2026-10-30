package com.example.seatinventory.scenario;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.seatinventory.application.dto.OpenSectionCommand;
import com.example.seatinventory.application.dto.ReserveSeatCommand;
import com.example.seatinventory.application.port.in.OpenSectionUseCase;
import com.example.seatinventory.application.port.out.IdGeneratorPort;
import com.example.seatinventory.application.port.out.SeatHoldRepositoryPort;
import com.example.seatinventory.application.port.out.SeatInventoryPort;
import com.example.seatinventory.application.usecase.ReserveSeatService;
import com.example.seatinventory.support.ParallelCallers;
import com.example.seatinventory.support.PostgresIntegrationTest;

/**
 * PRD failure scenario "two students compete for the last seat", with before and after numbers.
 * Not part of the build. Run it on demand: {@code ./mvnw verify -Dit.test=OversellScenario}
 */
class OversellScenario extends PostgresIntegrationTest {

    private static final int SEATS = 10;
    private static final int CALLERS = 200;

    @Autowired
    private OpenSectionUseCase openSection;

    @Autowired
    private SeatInventoryPort atomicSeatInventory;

    @Autowired
    private SeatHoldRepositoryPort seatHolds;

    @Autowired
    private IdGeneratorPort idGenerator;

    @Autowired
    private Clock clock;

    @Autowired
    private JdbcClient jdbcClient;

    @Autowired
    private TransactionTemplate transaction;

    @Test
    void checkThenWriteOversells() {
        int heldSeats = runAgainst(
            "check-then-write", new CheckThenWriteSeatInventory(atomicSeatInventory, jdbcClient));

        assertThat(heldSeats).isGreaterThan(SEATS);
    }

    @Test
    void atomicClaimHoldsExactlyTheCapacity() {
        int heldSeats = runAgainst("atomic update", atomicSeatInventory);

        assertThat(heldSeats).isEqualTo(SEATS);
    }

    private int runAgainst(String label, SeatInventoryPort seatInventory) {
        ReserveSeatService reserveSeat = new ReserveSeatService(seatInventory, seatHolds, idGenerator, clock);
        UUID sectionId = openSection.openSection(new OpenSectionCommand(SEATS)).sectionId();

        ParallelCallers.Outcome outcome = ParallelCallers.run(CALLERS, callerIndex ->
            transaction.executeWithoutResult(status ->
                reserveSeat.reserveSeat(new ReserveSeatCommand(sectionId, new UUID(0, callerIndex)))));

        int heldSeats = seatHoldsOf(sectionId);
        System.out.printf(
            "SCENARIO %s: seats=%d callers=%d accepted=%d rejected=%d heldSeats=%d reservedSeats=%d "
                + "elapsed=%dms p50=%dms p99=%dms%n",
            label, SEATS, CALLERS, outcome.accepted(), outcome.rejected(), heldSeats, reservedSeatsOf(sectionId),
            outcome.elapsedMillis(), outcome.latencyPercentile(50), outcome.latencyPercentile(99));
        return heldSeats;
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
