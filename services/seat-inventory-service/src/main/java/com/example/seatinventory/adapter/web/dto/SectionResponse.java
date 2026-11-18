package com.example.seatinventory.adapter.web.dto;

import java.util.UUID;

import com.example.seatinventory.application.dto.SectionResult;

public record SectionResponse(UUID sectionId, String code, String title, int capacity, int seatsLeft) {

    public static SectionResponse from(SectionResult result) {
        return new SectionResponse(
            result.sectionId(), result.code(), result.title(), result.capacity(), result.seatsLeft());
    }
}
