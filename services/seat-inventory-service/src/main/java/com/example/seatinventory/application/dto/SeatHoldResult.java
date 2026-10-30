package com.example.seatinventory.application.dto;

import java.time.Instant;
import java.util.UUID;

import com.example.seatinventory.domain.model.SeatHold;

public record SeatHoldResult(UUID holdId, UUID sectionId, UUID studentId, Instant heldAt) {

    public static SeatHoldResult from(SeatHold seatHold) {
        return new SeatHoldResult(seatHold.id(), seatHold.sectionId(), seatHold.studentId(), seatHold.heldAt());
    }
}
