package com.example.seatinventory.application.dto;

import java.time.Instant;
import java.util.UUID;

import com.example.seatinventory.domain.model.SeatHold;
import com.example.seatinventory.domain.model.Section;

public record StudentHoldResult(
    UUID holdId, UUID sectionId, String sectionCode, String sectionTitle, Instant heldAt, Instant expiresAt,
    String status
) {

    public static StudentHoldResult from(SeatHold seatHold, Section section) {
        return new StudentHoldResult(seatHold.id(), section.id(), section.code(), section.title(), seatHold.heldAt(),
            seatHold.expiresAt(), seatHold.status().name());
    }
}
