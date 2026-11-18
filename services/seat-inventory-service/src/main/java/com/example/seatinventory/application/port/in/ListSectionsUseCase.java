package com.example.seatinventory.application.port.in;

import java.util.List;

import com.example.seatinventory.application.dto.SectionResult;

/**
 * Lists every section with the seats left, ordered by code, so that students can pick one.
 */
public interface ListSectionsUseCase {

    List<SectionResult> listSections();
}
