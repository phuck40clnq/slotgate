package com.example.seatinventory.adapter.web;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.seatinventory.adapter.web.dto.OpenSectionRequest;
import com.example.seatinventory.adapter.web.dto.SeatHoldResponse;
import com.example.seatinventory.adapter.web.dto.SectionResponse;
import com.example.seatinventory.application.dto.OpenSectionCommand;
import com.example.seatinventory.application.dto.ReserveSeatCommand;
import com.example.seatinventory.application.port.in.GetSectionUseCase;
import com.example.seatinventory.application.port.in.OpenSectionUseCase;
import com.example.seatinventory.application.port.in.ReserveSeatUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/seat-inventory/sections")
@RequiredArgsConstructor
public class SectionController {

    private final OpenSectionUseCase openSectionUseCase;
    private final GetSectionUseCase getSectionUseCase;
    private final ReserveSeatUseCase reserveSeatUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SectionResponse openSection(@Valid @RequestBody OpenSectionRequest request) {
        return SectionResponse.from(openSectionUseCase.openSection(new OpenSectionCommand(request.capacity())));
    }

    @GetMapping("/{sectionId}")
    public SectionResponse getSection(@PathVariable UUID sectionId) {
        return SectionResponse.from(getSectionUseCase.getSection(sectionId));
    }

    @PostMapping("/{sectionId}/holds")
    @ResponseStatus(HttpStatus.CREATED)
    public SeatHoldResponse reserveSeat(
        @PathVariable UUID sectionId,
        @RequestHeader("X-Internal-User-Id") UUID studentId
    ) {
        return SeatHoldResponse.from(reserveSeatUseCase.reserveSeat(new ReserveSeatCommand(sectionId, studentId)));
    }
}
