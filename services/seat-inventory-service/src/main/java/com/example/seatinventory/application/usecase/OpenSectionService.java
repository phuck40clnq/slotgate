package com.example.seatinventory.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.seatinventory.application.dto.OpenSectionCommand;
import com.example.seatinventory.application.dto.SectionResult;
import com.example.seatinventory.application.port.in.OpenSectionUseCase;
import com.example.seatinventory.application.port.out.IdGeneratorPort;
import com.example.seatinventory.application.port.out.SeatInventoryPort;
import com.example.seatinventory.domain.model.Section;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenSectionService implements OpenSectionUseCase {

    private final SeatInventoryPort seatInventory;
    private final IdGeneratorPort idGenerator;

    @Override
    @Transactional
    public SectionResult openSection(OpenSectionCommand command) {
        Section section = Section.open(idGenerator.nextId(), command.capacity());
        seatInventory.save(section);
        return SectionResult.from(section);
    }
}
