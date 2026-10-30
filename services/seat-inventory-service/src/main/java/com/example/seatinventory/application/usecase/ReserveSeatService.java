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

    @Override
    @Transactional
    public SeatHoldResult reserveSeat(ReserveSeatCommand command) {
        return switch (seatInventory.claimSeat(command.sectionId())) {
            case SeatClaim.Claimed() -> holdSeat(command);
            case SeatClaim.SectionFull() -> throw rejected(SeatInventoryBusinessErrorCode.SECTION_FULL, command);
            case SeatClaim.SectionNotFound() ->
                throw rejected(SeatInventoryBusinessErrorCode.SECTION_NOT_FOUND, command);
        };
    }

    private SeatHoldResult holdSeat(ReserveSeatCommand command) {
        SeatHold seatHold = new SeatHold(
            idGenerator.nextId(), command.sectionId(), command.studentId(), clock.instant());
        seatHolds.save(seatHold);
        return SeatHoldResult.from(seatHold);
    }

    private BusinessException rejected(SeatInventoryBusinessErrorCode errorCode, ReserveSeatCommand command) {
        return new BusinessException(errorCode, Map.of("sectionId", command.sectionId()));
    }
}
