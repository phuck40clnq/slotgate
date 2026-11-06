package com.example.seatinventory.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.example.seatinventory.domain.model.SeatHold;

/**
 * Stores the seats held by students. A student holds at most one seat per section.
 */
public interface SeatHoldRepositoryPort {

    /**
     * Saves the hold unless the student already holds a seat in the section or the section does not exist.
     * Atomic: concurrent calls for the same student and section save at most one hold.
     *
     * @return whether the hold was saved
     */
    boolean saveIfAbsent(SeatHold seatHold);

    Optional<SeatHold> findHold(UUID sectionId, UUID studentId);
}
