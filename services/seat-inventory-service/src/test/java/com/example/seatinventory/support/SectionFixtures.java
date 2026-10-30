package com.example.seatinventory.support;

import java.util.UUID;

import com.example.seatinventory.domain.model.Section;

public final class SectionFixtures {

    public static final UUID SECTION_ID = UUID.fromString("00000000-0000-0000-0000-00000000000a");
    public static final UUID STUDENT_ID = UUID.fromString("00000000-0000-0000-0000-00000000000b");
    public static final UUID HOLD_ID = UUID.fromString("00000000-0000-0000-0000-00000000000c");

    private SectionFixtures() {
    }

    public static Section freeSection() {
        return new Section(SECTION_ID, 40, 0);
    }

    public static Section fullSection() {
        return new Section(SECTION_ID, 40, 40);
    }
}
