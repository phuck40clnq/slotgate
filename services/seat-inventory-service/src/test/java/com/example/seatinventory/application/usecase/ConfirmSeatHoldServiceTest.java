package com.example.seatinventory.application.usecase;

import static com.example.seatinventory.support.SectionFixtures.HOLD_ID;
import static com.example.seatinventory.support.SectionFixtures.SECTION_ID;
import static com.example.seatinventory.support.SectionFixtures.STUDENT_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.example.seatinventory.application.dto.ConfirmSeatHoldCommand;
import com.example.seatinventory.application.dto.SeatHoldResult;
import com.example.seatinventory.common.error.SeatInventoryBusinessErrorCode;
import com.example.seatinventory.common.exception.BusinessException;
import com.example.seatinventory.domain.model.SeatHold;
import com.example.seatinventory.domain.model.SeatHoldStatus;
import com.example.seatinventory.support.InMemorySeatHolds;
import com.example.seatinventory.support.InMemorySeatInventory;

class ConfirmSeatHoldServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-14T12:00:00Z");
    private static final Instant HELD_AT = NOW.minus(Duration.ofMinutes(1));
    private static final UUID OTHER_ID = UUID.fromString("00000000-0000-0000-0000-0000000000ff");
    private static final ConfirmSeatHoldCommand COMMAND = new ConfirmSeatHoldCommand(SECTION_ID, HOLD_ID, STUDENT_ID);

    private final InMemorySeatHolds seatHolds = new InMemorySeatHolds(new InMemorySeatInventory());
    private final ConfirmSeatHoldService service = new ConfirmSeatHoldService(
        seatHolds, Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void confirmsAHeldHoldBeforeItExpires() {
        seatHolds.add(hold(NOW.plus(Duration.ofMinutes(4)), SeatHoldStatus.HELD));

        SeatHoldResult result = service.confirmSeatHold(COMMAND);

        assertThat(result.status()).isEqualTo("CONFIRMED");
        assertThat(seatHolds.findHold(HOLD_ID)).get().returns(SeatHoldStatus.CONFIRMED, SeatHold::status);
    }

    @Test
    void returnsTheSameConfirmedHoldWhenConfirmedAgain() {
        seatHolds.add(hold(NOW.plus(Duration.ofMinutes(4)), SeatHoldStatus.HELD));
        SeatHoldResult firstResult = service.confirmSeatHold(COMMAND);

        SeatHoldResult secondResult = service.confirmSeatHold(COMMAND);

        assertThat(secondResult).isEqualTo(firstResult);
    }

    @Test
    void rejectsAHeldHoldWhoseTimeIsUp() {
        seatHolds.add(hold(NOW, SeatHoldStatus.HELD));

        assertRejectedWith(SeatInventoryBusinessErrorCode.HOLD_EXPIRED, COMMAND);
        assertThat(seatHolds.findHold(HOLD_ID)).get().returns(SeatHoldStatus.HELD, SeatHold::status);
    }

    @Test
    void rejectsAnExpiredHold() {
        seatHolds.add(hold(NOW.minus(Duration.ofSeconds(1)), SeatHoldStatus.EXPIRED));

        assertRejectedWith(SeatInventoryBusinessErrorCode.HOLD_EXPIRED, COMMAND);
    }

    @Test
    void rejectsAnUnknownHold() {
        assertRejectedWith(SeatInventoryBusinessErrorCode.HOLD_NOT_FOUND, COMMAND);
    }

    @Test
    void rejectsTheHoldOfAnotherStudent() {
        seatHolds.add(hold(NOW.plus(Duration.ofMinutes(4)), SeatHoldStatus.HELD));

        assertRejectedWith(SeatInventoryBusinessErrorCode.HOLD_NOT_FOUND,
            new ConfirmSeatHoldCommand(SECTION_ID, HOLD_ID, OTHER_ID));
        assertThat(seatHolds.findHold(HOLD_ID)).get().returns(SeatHoldStatus.HELD, SeatHold::status);
    }

    @Test
    void rejectsAHoldOutsideTheSection() {
        seatHolds.add(hold(NOW.plus(Duration.ofMinutes(4)), SeatHoldStatus.HELD));

        assertRejectedWith(SeatInventoryBusinessErrorCode.HOLD_NOT_FOUND,
            new ConfirmSeatHoldCommand(OTHER_ID, HOLD_ID, STUDENT_ID));
    }

    private void assertRejectedWith(SeatInventoryBusinessErrorCode errorCode, ConfirmSeatHoldCommand command) {
        BusinessException rejection = catchThrowableOfType(
            BusinessException.class, () -> service.confirmSeatHold(command));

        assertThat(rejection.getErrorCode()).isEqualTo(errorCode);
    }

    private SeatHold hold(Instant expiresAt, SeatHoldStatus status) {
        return new SeatHold(HOLD_ID, SECTION_ID, STUDENT_ID, HELD_AT, expiresAt, status);
    }
}
