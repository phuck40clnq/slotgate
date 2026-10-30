package com.example.seatinventory.application.port.in;

import java.util.UUID;

import com.example.seatinventory.application.dto.SectionResult;

/**
 * Reads a section and the seats left in it.
 */
public interface GetSectionUseCase {

    /**
     * @throws com.example.seatinventory.common.exception.BusinessException SECTION_NOT_FOUND
     */
    SectionResult getSection(UUID sectionId);
}
