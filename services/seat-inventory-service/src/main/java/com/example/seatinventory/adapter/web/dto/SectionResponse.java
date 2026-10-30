package com.example.seatinventory.adapter.web.dto;

import java.util.UUID;

import com.example.seatinventory.application.dto.SectionResult;

public record SectionResponse(UUID sectionId, int capacity, int seatsLeft) {

    public static SectionResponse from(SectionResult result) {
        return new SectionResponse(result.sectionId(), result.capacity(), result.seatsLeft());
    }
}
