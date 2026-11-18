package com.example.seatinventory.application.usecase;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.seatinventory.application.dto.StudentHoldResult;
import com.example.seatinventory.application.port.in.ListStudentHoldsUseCase;
import com.example.seatinventory.application.port.out.SeatHoldRepositoryPort;
import com.example.seatinventory.application.port.out.SeatInventoryPort;
import com.example.seatinventory.domain.model.SeatHold;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListStudentHoldsService implements ListStudentHoldsUseCase {

    private final SeatHoldRepositoryPort seatHolds;
    private final SeatInventoryPort seatInventory;
    private final Clock clock;

    /**
     * A student has at most one current hold per section (ADR-0006), so the holds are keyed by section and take
     * the order of their sections. The two reads share no transaction: a hold confirmed between them is listed as
     * held, a staleness the next read clears.
     */
    @Override
    public List<StudentHoldResult> listStudentHolds(UUID studentId) {
        Map<UUID, SeatHold> holdsBySection = seatHolds.findCurrentHolds(studentId, clock.instant()).stream()
            .collect(Collectors.toMap(SeatHold::sectionId, Function.identity()));
        return seatInventory.findSections(holdsBySection.keySet()).stream()
            .map(section -> StudentHoldResult.from(holdsBySection.get(section.id()), section))
            .toList();
    }
}
