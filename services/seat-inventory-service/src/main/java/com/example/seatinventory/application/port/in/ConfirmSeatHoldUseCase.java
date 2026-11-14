package com.example.seatinventory.application.port.in;

import com.example.seatinventory.application.dto.ConfirmSeatHoldCommand;
import com.example.seatinventory.application.dto.SeatHoldResult;

/**
 * Confirms a student's hold before it expires, so that the seat stays theirs. Repeating the request returns the
 * confirmed hold.
 */
public interface ConfirmSeatHoldUseCase {

    /**
     * @throws com.example.seatinventory.common.exception.BusinessException HOLD_NOT_FOUND
     * @throws com.example.seatinventory.common.exception.BusinessException HOLD_EXPIRED
     */
    SeatHoldResult confirmSeatHold(ConfirmSeatHoldCommand command);
}
