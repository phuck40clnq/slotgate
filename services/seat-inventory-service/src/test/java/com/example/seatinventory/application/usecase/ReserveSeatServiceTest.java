package com.example.seatinventory.application.usecase;

import static com.example.seatinventory.support.SectionFixtures.HOLD_ID;
import static com.example.seatinventory.support.SectionFixtures.SECTION_ID;
import static com.example.seatinventory.support.SectionFixtures.STUDENT_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import com.example.seatinventory.application.dto.ReserveSeatCommand;
import com.example.seatinventory.application.dto.SeatHoldResult;
import com.example.seatinventory.common.error.SeatInventoryBusinessErrorCode;
import com.example.seatinventory.common.exception.BusinessException;
import com.example.seatinventory.domain.model.SeatHold;
import com.example.seatinventory.support.InMemorySeatHolds;
import com.example.seatinventory.support.InMemorySeatInventory;
import com.example.seatinventory.support.SectionFixtures;

class ReserveSeatServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-30T12:00:00Z");
    private static final ReserveSeatCommand COMMAND = new ReserveSeatCommand(SECTION_ID, STUDENT_ID);

    private final InMemorySeatInventory seatInventory = new InMemorySeatInventory();
    private final InMemorySeatHolds seatHolds = new InMemorySeatHolds();
    private final ReserveSeatService service = new ReserveSeatService(
        seatInventory, seatHolds, () -> HOLD_ID, Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void holdsSeatForStudent() {
        seatInventory.save(SectionFixtures.freeSection());

        SeatHoldResult result = service.reserveSeat(COMMAND);

        assertThat(result).isEqualTo(new SeatHoldResult(HOLD_ID, SECTION_ID, STUDENT_ID, NOW));
        assertThat(seatHolds.all()).containsExactly(new SeatHold(HOLD_ID, SECTION_ID, STUDENT_ID, NOW));
    }

    @Test
    void takesOneSeatFromTheSection() {
        seatInventory.save(SectionFixtures.freeSection());

        service.reserveSeat(COMMAND);

        assertThat(seatInventory.findSection(SECTION_ID)).get()
            .returns(39, section -> section.seatsLeft());
    }

    @Test
    void rejectsHoldWhenSectionIsFull() {
        seatInventory.save(SectionFixtures.fullSection());

        BusinessException rejection = catchThrowableOfType(BusinessException.class, () -> service.reserveSeat(COMMAND));

        assertThat(rejection.getErrorCode()).isEqualTo(SeatInventoryBusinessErrorCode.SECTION_FULL);
        assertThat(seatHolds.all()).isEmpty();
    }

    @Test
    void rejectsHoldWhenSectionDoesNotExist() {
        BusinessException rejection = catchThrowableOfType(BusinessException.class, () -> service.reserveSeat(COMMAND));

        assertThat(rejection.getErrorCode()).isEqualTo(SeatInventoryBusinessErrorCode.SECTION_NOT_FOUND);
        assertThat(seatHolds.all()).isEmpty();
    }
}
