package com.example.seatinventory.support;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.example.seatinventory.application.port.out.SeatInventoryPort;
import com.example.seatinventory.domain.model.SeatClaim;
import com.example.seatinventory.domain.model.Section;

public class InMemorySeatInventory implements SeatInventoryPort {

    private final Map<UUID, Section> sections = new HashMap<>();

    @Override
    public boolean saveIfCodeIsFree(Section section) {
        boolean isCodeTaken = sections.values().stream().anyMatch(stored -> stored.code().equals(section.code()));
        if (isCodeTaken) {
            return false;
        }
        save(section);
        return true;
    }

    @Override
    public Optional<Section> findSection(UUID sectionId) {
        return Optional.ofNullable(sections.get(sectionId));
    }

    @Override
    public List<Section> listSections() {
        return sections.values().stream()
            .sorted(Comparator.comparing(Section::code))
            .toList();
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
        save(withReservedSeats(section, section.reservedSeats() + 1));
        return new SeatClaim.Claimed();
    }

    @Override
    public void releaseSeats(UUID sectionId, int seats) {
        Section section = sections.get(sectionId);
        save(withReservedSeats(section, section.reservedSeats() - seats));
    }

    /**
     * Stores the section as given, for tests that arrange a state.
     */
    public void save(Section section) {
        sections.put(section.id(), section);
    }

    private static Section withReservedSeats(Section section, int reservedSeats) {
        return new Section(section.id(), section.code(), section.title(), section.capacity(), reservedSeats);
    }
}
