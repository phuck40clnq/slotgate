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
    private final InMemorySeatHolds seatHolds = new InMemorySeatHolds(seatInventory);
    private final ReserveSeatService service = new ReserveSeatService(
        seatInventory, seatHolds, () -> HOLD_ID, Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void holdsSeatForStudent() {
        seatInventory.save(SectionFixtures.freeSection());

        SeatHoldResult result = service.reserveSeat(COMMAND);

        assertThat(result).isEqualTo(new SeatHoldResult(HOLD_ID, SECTION_ID, STUDENT_ID, NOW, true));
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
    void returnsTheExistingHoldWhenTheStudentAsksAgain() {
        seatInventory.save(SectionFixtures.freeSection());
        service.reserveSeat(COMMAND);

        SeatHoldResult result = service.reserveSeat(COMMAND);

        assertThat(result).isEqualTo(new SeatHoldResult(HOLD_ID, SECTION_ID, STUDENT_ID, NOW, false));
    }

    @Test
    void takesNoSecondSeatWhenTheStudentAsksAgain() {
        seatInventory.save(SectionFixtures.freeSection());
        service.reserveSeat(COMMAND);

        service.reserveSeat(COMMAND);

        assertThat(seatHolds.all()).hasSize(1);
        assertThat(seatInventory.findSection(SECTION_ID)).get()
            .returns(39, section -> section.seatsLeft());
    }

    @Test
    void returnsTheExistingHoldWhenTheStudentAsksAgainForTheLastSeat() {
        seatInventory.save(SectionFixtures.sectionWithOneSeatLeft());
        service.reserveSeat(COMMAND);

        SeatHoldResult result = service.reserveSeat(COMMAND);

        assertThat(result.isNewHold()).isFalse();
        assertThat(result.holdId()).isEqualTo(HOLD_ID);
    }

    @Test
    void rejectsHoldWhenSectionIsFull() {
        seatInventory.save(SectionFixtures.fullSection());

        BusinessException rejection = catchThrowableOfType(BusinessException.class, () -> service.reserveSeat(COMMAND));

        assertThat(rejection.getErrorCode()).isEqualTo(SeatInventoryBusinessErrorCode.SECTION_FULL);
    }

    @Test
    void rejectsHoldWhenSectionDoesNotExist() {
        BusinessException rejection = catchThrowableOfType(BusinessException.class, () -> service.reserveSeat(COMMAND));

        assertThat(rejection.getErrorCode()).isEqualTo(SeatInventoryBusinessErrorCode.SECTION_NOT_FOUND);
        assertThat(seatHolds.all()).isEmpty();
    }
}
