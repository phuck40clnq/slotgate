package com.example.seatinventory.adapter.web.dto;

import java.util.List;

import com.example.seatinventory.application.dto.SectionResult;

public record SectionsResponse(List<SectionResponse> sections) {

    public static SectionsResponse from(List<SectionResult> results) {
        return new SectionsResponse(results.stream().map(SectionResponse::from).toList());
    }
}
