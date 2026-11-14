package com.example.seatinventory.application.usecase;

import java.time.Clock;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.seatinventory.application.dto.ConfirmSeatHoldCommand;
import com.example.seatinventory.application.dto.SeatHoldResult;
import com.example.seatinventory.application.port.in.ConfirmSeatHoldUseCase;
import com.example.seatinventory.application.port.out.SeatHoldRepositoryPort;
import com.example.seatinventory.common.error.SeatInventoryBusinessErrorCode;
import com.example.seatinventory.common.exception.BusinessException;
import com.example.seatinventory.domain.model.SeatHold;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConfirmSeatHoldService implements ConfirmSeatHoldUseCase {

    private final SeatHoldRepositoryPort seatHolds;
    private final Clock clock;

    /**
     * Confirming is one conditional update, so it cannot interleave with an expiry of the same hold. When the
     * update changes nothing, the hold is read again: an earlier or concurrent confirmation of the same hold
     * returns the confirmed hold, anything else means it expired. See ADR-0007.
     */
    @Override
    @Transactional
    public SeatHoldResult confirmSeatHold(ConfirmSeatHoldCommand command) {
        SeatHold seatHold = holdOfTheStudent(command);
        if (seatHolds.confirm(seatHold.id(), clock.instant())) {
            return SeatHoldResult.existingHold(seatHold.confirmed());
        }
        return seatHolds.findHold(seatHold.id())
            .filter(SeatHold::isConfirmed)
            .map(SeatHoldResult::existingHold)
            .orElseThrow(() -> rejected(SeatInventoryBusinessErrorCode.HOLD_EXPIRED, command));
    }

    private SeatHold holdOfTheStudent(ConfirmSeatHoldCommand command) {
        return seatHolds.findHold(command.holdId())
            .filter(seatHold -> seatHold.sectionId().equals(command.sectionId()))
            .filter(seatHold -> seatHold.studentId().equals(command.studentId()))
            .orElseThrow(() -> rejected(SeatInventoryBusinessErrorCode.HOLD_NOT_FOUND, command));
    }

    private BusinessException rejected(SeatInventoryBusinessErrorCode errorCode, ConfirmSeatHoldCommand command) {
        return new BusinessException(errorCode, Map.of("sectionId", command.sectionId(), "holdId", command.holdId()));
    }
}
