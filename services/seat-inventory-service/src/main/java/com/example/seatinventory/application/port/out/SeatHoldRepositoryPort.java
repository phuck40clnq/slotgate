package com.example.seatinventory.application.port.out;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.example.seatinventory.domain.model.SeatHold;

/**
 * Stores the seats held by students. A student has at most one active (held or confirmed) hold per section.
 */
public interface SeatHoldRepositoryPort {

    /**
     * Saves the hold unless the student already has an active hold in the section or the section does not
     * exist. Atomic: concurrent calls for the same student and section save at most one hold.
     *
     * @return whether the hold was saved
     */
    boolean saveIfAbsent(SeatHold seatHold);

    Optional<SeatHold> findActiveHold(UUID sectionId, UUID studentId);

    Optional<SeatHold> findHold(UUID holdId);

    /**
     * Confirms the hold if it is still held and has not expired at {@code now}. Atomic: of concurrent
     * confirmations and expiries of one hold, exactly one changes it.
     *
     * @return whether this call confirmed the hold
     */
    boolean confirm(UUID holdId, Instant now);
}
