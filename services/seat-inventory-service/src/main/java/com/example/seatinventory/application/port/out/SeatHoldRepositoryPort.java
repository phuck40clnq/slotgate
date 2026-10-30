package com.example.seatinventory.application.port.out;

import com.example.seatinventory.domain.model.SeatHold;

/**
 * Stores the seats held by students.
 */
public interface SeatHoldRepositoryPort {

    void save(SeatHold seatHold);
}
