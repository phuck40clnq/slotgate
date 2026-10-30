package com.example.seatinventory.adapter.web.dto;

import java.time.Instant;
import java.util.UUID;

import com.example.seatinventory.application.dto.SeatHoldResult;

public record SeatHoldResponse(UUID holdId, UUID sectionId, UUID studentId, Instant heldAt) {

    public static SeatHoldResponse from(SeatHoldResult result) {
        return new SeatHoldResponse(result.holdId(), result.sectionId(), result.studentId(), result.heldAt());
    }
}
