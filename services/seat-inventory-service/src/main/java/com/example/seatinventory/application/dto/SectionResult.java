package com.example.seatinventory.application.dto;

import java.util.UUID;

import com.example.seatinventory.domain.model.Section;

public record SectionResult(UUID sectionId, String code, String title, int capacity, int seatsLeft) {

    public static SectionResult from(Section section) {
        return new SectionResult(
            section.id(), section.code(), section.title(), section.capacity(), section.seatsLeft());
    }
}
