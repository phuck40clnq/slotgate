package com.example.seatinventory.adapter.web.dto;

import java.time.Instant;
import java.util.UUID;

import com.example.seatinventory.application.dto.StudentHoldResult;

public record StudentHoldResponse(
    UUID holdId, UUID sectionId, String sectionCode, String sectionTitle, Instant heldAt, Instant expiresAt,
    String status
) {

    public static StudentHoldResponse from(StudentHoldResult result) {
        return new StudentHoldResponse(result.holdId(), result.sectionId(), result.sectionCode(),
            result.sectionTitle(), result.heldAt(), result.expiresAt(), result.status());
    }
}
