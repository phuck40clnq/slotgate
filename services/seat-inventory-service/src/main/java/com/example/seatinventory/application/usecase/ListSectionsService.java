package com.example.seatinventory.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.seatinventory.application.dto.SectionResult;
import com.example.seatinventory.application.port.in.ListSectionsUseCase;
import com.example.seatinventory.application.port.out.SeatInventoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListSectionsService implements ListSectionsUseCase {

    private final SeatInventoryPort seatInventory;

    @Override
    public List<SectionResult> listSections() {
        return seatInventory.listSections().stream()
            .map(SectionResult::from)
            .toList();
    }
}
