package com.example.seatinventory.application.usecase;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.seatinventory.application.dto.SectionResult;
import com.example.seatinventory.application.port.in.GetSectionUseCase;
import com.example.seatinventory.application.port.out.SeatInventoryPort;
import com.example.seatinventory.common.error.SeatInventoryBusinessErrorCode;
import com.example.seatinventory.common.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetSectionService implements GetSectionUseCase {

    private final SeatInventoryPort seatInventory;

    @Override
    public SectionResult getSection(UUID sectionId) {
        return seatInventory.findSection(sectionId)
            .map(SectionResult::from)
            .orElseThrow(() -> new BusinessException(
                SeatInventoryBusinessErrorCode.SECTION_NOT_FOUND, Map.of("sectionId", sectionId)));
    }
}
