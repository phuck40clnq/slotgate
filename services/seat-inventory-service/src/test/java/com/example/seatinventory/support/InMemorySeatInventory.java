package com.example.seatinventory.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.example.seatinventory.application.port.out.SeatInventoryPort;
import com.example.seatinventory.domain.model.SeatClaim;
import com.example.seatinventory.domain.model.Section;

public class InMemorySeatInventory implements SeatInventoryPort {

    private final Map<UUID, Section> sections = new HashMap<>();

    @Override
    public void save(Section section) {
        sections.put(section.id(), section);
    }

    @Override
    public Optional<Section> findSection(UUID sectionId) {
        return Optional.ofNullable(sections.get(sectionId));
    }

    @Override
    public SeatClaim claimSeat(UUID sectionId) {
        Section section = sections.get(sectionId);
        if (section == null) {
            return new SeatClaim.SectionNotFound();
        }
        if (section.seatsLeft() == 0) {
            return new SeatClaim.SectionFull();
        }
        save(new Section(section.id(), section.capacity(), section.reservedSeats() + 1));
        return new SeatClaim.Claimed();
    }
}
