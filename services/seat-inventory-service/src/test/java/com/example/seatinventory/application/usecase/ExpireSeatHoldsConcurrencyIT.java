package com.example.seatinventory.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.seatinventory.application.port.out.SeatHoldRepositoryPort;
import com.example.seatinventory.support.ConfirmExpiryRace;

class ExpireSeatHoldsConcurrencyIT extends ConfirmExpiryRace {

    @Autowired
    private SeatHoldRepositoryPort seatHolds;

    @ParameterizedTest(name = "{0} holds")
    @ValueSource(ints = {1, 200})
    void confirmationsRacingASweepKeepTheSeatCountExact(int holds) {
        Outcome outcome = race(seatHolds, holds);

        assertThat(outcome.isCorrect()).as("%s", outcome).isTrue();
    }
}
