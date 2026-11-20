package com.example.seatinventory.support;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.seatinventory.application.dto.OpenSectionCommand;
import com.example.seatinventory.domain.model.Section;

public final class SectionFixtures {

    public static final UUID SECTION_ID = UUID.fromString("00000000-0000-0000-0000-00000000000a");
    public static final UUID STUDENT_ID = UUID.fromString("00000000-0000-0000-0000-00000000000b");
    public static final UUID HOLD_ID = UUID.fromString("00000000-0000-0000-0000-00000000000c");
    public static final String SECTION_CODE = "SIM101 1";
    public static final String SECTION_TITLE = "Simulated course";

    private static final AtomicInteger NEXT_CODE = new AtomicInteger();

    private SectionFixtures() {
    }

    public static Section freeSection() {
        return new Section(SECTION_ID, SECTION_CODE, SECTION_TITLE, 40, 0);
    }

    public static Section sectionWithOneSeatLeft() {
        return new Section(SECTION_ID, SECTION_CODE, SECTION_TITLE, 40, 39);
    }

    public static Section fullSection() {
        return new Section(SECTION_ID, SECTION_CODE, SECTION_TITLE, 40, 40);
    }

    /**
     * A section with a code of its own, so that several can be stored in one database.
     */
    public static Section section(UUID id, int capacity, int reservedSeats) {
        return new Section(id, nextCode(), SECTION_TITLE, capacity, reservedSeats);
    }

    /**
     * A command with a code of its own, so that several sections can be opened in one database.
     */
    public static OpenSectionCommand openSectionCommand(int capacity) {
        return new OpenSectionCommand(nextCode(), SECTION_TITLE, capacity);
    }

    private static String nextCode() {
        return "SIM" + NEXT_CODE.incrementAndGet();
    }
}
