package com.example.seatinventory.adapter.web.dto;

import java.util.List;

import com.example.seatinventory.application.dto.StudentHoldResult;

public record StudentHoldsResponse(List<StudentHoldResponse> holds) {

    public static StudentHoldsResponse from(List<StudentHoldResult> results) {
        return new StudentHoldsResponse(results.stream().map(StudentHoldResponse::from).toList());
    }
}
