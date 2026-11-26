package com.example.seatinventory.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.annotation.Import;

import com.example.seatinventory.application.port.out.SeatHoldRepositoryPort;
import com.example.seatinventory.support.ConfirmExpiryRace;
import com.example.seatinventory.support.ConfirmExpiryRace.Outcome;
import com.example.seatinventory.support.PostgresIntegrationTest;

@Import(ConfirmExpiryRace.class)
@RequiredArgsConstructor
class ExpireSeatHoldsConcurrencyIT extends PostgresIntegrationTest {

    private final ConfirmExpiryRace confirmExpiryRace;

    private final SeatHoldRepositoryPort seatHolds;

    @ParameterizedTest(name = "{0} holds")
    @ValueSource(ints = {1, 200})
    void confirmationsRacingASweepKeepTheSeatCountExact(int holds) {
        Outcome outcome = confirmExpiryRace.race(seatHolds, holds);

        assertThat(outcome.isCorrect()).as("%s", outcome).isTrue();
    }
}
