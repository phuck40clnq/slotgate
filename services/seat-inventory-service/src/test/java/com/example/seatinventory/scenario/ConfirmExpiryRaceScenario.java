package com.example.seatinventory.scenario;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;

import com.example.seatinventory.application.port.out.SeatHoldRepositoryPort;
import com.example.seatinventory.support.ConfirmExpiryRace;

/**
 * PRD failure scenario "phantom holds", second half: a student confirms at the moment the sweep expires the
 * hold. Each hold must end either confirmed with its seat or expired without it, and the student must be told
 * which. Not part of the build. Run it on demand: {@code ./mvnw verify -Dit.test=ConfirmExpiryRaceScenario}
 */
class ConfirmExpiryRaceScenario extends ConfirmExpiryRace {

    private static final int HOLDS = 200;
    private static final int RUNS = 3;

    @Autowired
    private SeatHoldRepositoryPort conditionalSeatHolds;

    @Autowired
    private JdbcClient jdbcClient;

    @Test
    void checkThenWriteBreaksTheOutcome() {
        List<Outcome> outcomes =
            runAgainst("check-then-write", new CheckThenWriteSeatHolds(conditionalSeatHolds, jdbcClient));

        assertThat(outcomes).anyMatch(outcome -> !outcome.isCorrect());
    }

    @Test
    void conditionalUpdatesKeepTheOutcomeCorrect() {
        List<Outcome> outcomes = runAgainst("conditional update", conditionalSeatHolds);

        assertThat(outcomes).allMatch(Outcome::isCorrect);
    }

    private List<Outcome> runAgainst(String label, SeatHoldRepositoryPort seatHolds) {
        return IntStream.rangeClosed(1, RUNS)
            .mapToObj(run -> report(label, run, race(seatHolds, HOLDS)))
            .toList();
    }

    private Outcome report(String label, int run, Outcome outcome) {
        System.out.printf(
            "SCENARIO %s run %d: holds=%d toldConfirmed=%d toldExpired=%d confirmedInDb=%d expiredInDb=%d "
                + "reservedSeats=%d confirmedWithoutSeat=%d correct=%b%n",
            label, run, outcome.holds(), outcome.confirmedByCallers(), outcome.expiredByCallers(),
            outcome.confirmedHolds(), outcome.expiredHolds(), outcome.reservedSeats(),
            outcome.confirmedHolds() - outcome.reservedSeats(), outcome.isCorrect());
        return outcome;
    }
}
