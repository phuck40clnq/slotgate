package com.example.seatinventory.application.usecase;

import java.time.Clock;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.seatinventory.application.dto.ReserveSeatCommand;
import com.example.seatinventory.application.dto.SeatHoldResult;
import com.example.seatinventory.application.port.in.ReserveSeatUseCase;
import com.example.seatinventory.application.port.out.IdGeneratorPort;
import com.example.seatinventory.application.port.out.SeatHoldRepositoryPort;
import com.example.seatinventory.application.port.out.SeatInventoryPort;
import com.example.seatinventory.common.error.SeatInventoryBusinessErrorCode;
import com.example.seatinventory.common.exception.BusinessException;
import com.example.seatinventory.domain.model.SeatClaim;
import com.example.seatinventory.domain.model.SeatHold;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReserveSeatService implements ReserveSeatUseCase {

    private final SeatInventoryPort seatInventory;
    private final SeatHoldRepositoryPort seatHolds;
    private final IdGeneratorPort idGenerator;
    private final Clock clock;

    /**
     * The hold is saved before the seat is claimed, so that a repeated request finds the existing hold even
     * when the section is full by now. A rejected claim rolls back the hold saved before it. See ADR-0006.
     */
    @Override
    @Transactional
    public SeatHoldResult reserveSeat(ReserveSeatCommand command) {
        SeatHold newHold = new SeatHold(
            idGenerator.nextId(), command.sectionId(), command.studentId(), clock.instant());
        if (!seatHolds.saveIfAbsent(newHold)) {
            return existingHold(command);
        }
        return claimSeatFor(newHold, command);
    }

    private SeatHoldResult existingHold(ReserveSeatCommand command) {
        return seatHolds.findHold(command.sectionId(), command.studentId())
            .map(SeatHoldResult::existingHold)
            .orElseThrow(() -> rejected(SeatInventoryBusinessErrorCode.SECTION_NOT_FOUND, command));
    }

    private SeatHoldResult claimSeatFor(SeatHold newHold, ReserveSeatCommand command) {
        return switch (seatInventory.claimSeat(command.sectionId())) {
            case SeatClaim.Claimed() -> SeatHoldResult.newHold(newHold);
            case SeatClaim.SectionFull() -> throw rejected(SeatInventoryBusinessErrorCode.SECTION_FULL, command);
            case SeatClaim.SectionNotFound() ->
                throw rejected(SeatInventoryBusinessErrorCode.SECTION_NOT_FOUND, command);
        };
    }

    private BusinessException rejected(SeatInventoryBusinessErrorCode errorCode, ReserveSeatCommand command) {
        return new BusinessException(errorCode, Map.of("sectionId", command.sectionId()));
    }
}
