package com.example.seatinventory.application.dto;

import java.time.Instant;
import java.util.UUID;

import com.example.seatinventory.domain.model.SeatHold;

public record SeatHoldResult(
    UUID holdId, UUID sectionId, UUID studentId, Instant heldAt, Instant expiresAt, String status, boolean isNewHold
) {

    public static SeatHoldResult newHold(SeatHold seatHold) {
        return new SeatHoldResult(seatHold.id(), seatHold.sectionId(), seatHold.studentId(), seatHold.heldAt(),
            seatHold.expiresAt(), seatHold.status().name(), true);
    }

    public static SeatHoldResult existingHold(SeatHold seatHold) {
        return new SeatHoldResult(seatHold.id(), seatHold.sectionId(), seatHold.studentId(), seatHold.heldAt(),
            seatHold.expiresAt(), seatHold.status().name(), false);
    }
}
