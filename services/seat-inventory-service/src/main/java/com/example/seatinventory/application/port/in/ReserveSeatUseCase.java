package com.example.seatinventory.application.port.in;

import com.example.seatinventory.application.dto.ReserveSeatCommand;
import com.example.seatinventory.application.dto.SeatHoldResult;

/**
 * Holds one seat of a section for a student. A student holds at most one seat per section, so repeating the
 * request returns the existing hold.
 */
public interface ReserveSeatUseCase {

    /**
     * @throws com.example.seatinventory.common.exception.BusinessException SECTION_FULL
     * @throws com.example.seatinventory.common.exception.BusinessException SECTION_NOT_FOUND
     */
    SeatHoldResult reserveSeat(ReserveSeatCommand command);
}
