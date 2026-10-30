package com.example.seatinventory.application.dto;

import java.util.UUID;

import com.example.seatinventory.domain.model.Section;

public record SectionResult(UUID sectionId, int capacity, int seatsLeft) {

    public static SectionResult from(Section section) {
        return new SectionResult(section.id(), section.capacity(), section.seatsLeft());
    }
}
