package com.example.seatinventory.adapter.web;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.seatinventory.adapter.web.dto.StudentHoldsResponse;
import com.example.seatinventory.application.port.in.ListStudentHoldsUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/seat-inventory/me/holds")
@RequiredArgsConstructor
public class StudentHoldController {

    private final ListStudentHoldsUseCase listStudentHoldsUseCase;

    @GetMapping
    public StudentHoldsResponse listStudentHolds(@RequestHeader("X-Internal-User-Id") UUID studentId) {
        return StudentHoldsResponse.from(listStudentHoldsUseCase.listStudentHolds(studentId));
    }
}
