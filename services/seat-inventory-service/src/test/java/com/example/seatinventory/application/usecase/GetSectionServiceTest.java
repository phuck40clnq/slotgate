package com.example.seatinventory.application.usecase;

import static com.example.seatinventory.support.SectionFixtures.SECTION_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import org.junit.jupiter.api.Test;

import com.example.seatinventory.application.dto.SectionResult;
import com.example.seatinventory.common.error.SeatInventoryBusinessErrorCode;
import com.example.seatinventory.common.exception.BusinessException;
import com.example.seatinventory.support.InMemorySeatInventory;
import com.example.seatinventory.support.SectionFixtures;

class GetSectionServiceTest {

    private final InMemorySeatInventory seatInventory = new InMemorySeatInventory();
    private final GetSectionService service = new GetSectionService(seatInventory);

    @Test
    void readsSeatsLeft() {
        seatInventory.save(SectionFixtures.freeSection());

        SectionResult result = service.getSection(SECTION_ID);

        assertThat(result).isEqualTo(new SectionResult(SECTION_ID, 40, 40));
    }

    @Test
    void rejectsUnknownSection() {
        BusinessException rejection = catchThrowableOfType(
            BusinessException.class, () -> service.getSection(SECTION_ID));

        assertThat(rejection.getErrorCode()).isEqualTo(SeatInventoryBusinessErrorCode.SECTION_NOT_FOUND);
    }
}
