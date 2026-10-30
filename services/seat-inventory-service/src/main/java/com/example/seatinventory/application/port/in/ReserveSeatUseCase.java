package com.example.seatinventory.application.port.in;

import com.example.seatinventory.application.dto.ReserveSeatCommand;
import com.example.seatinventory.application.dto.SeatHoldResult;

/**
 * Holds one seat of a section for a student.
 */
public interface ReserveSeatUseCase {

    /**
     * @throws com.example.seatinventory.common.exception.BusinessException SECTION_FULL
     * @throws com.example.seatinventory.common.exception.BusinessException SECTION_NOT_FOUND
     */
    SeatHoldResult reserveSeat(ReserveSeatCommand command);
}
