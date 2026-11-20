package com.example.seatinventory.application.port.in;

import java.util.List;
import java.util.UUID;

import com.example.seatinventory.application.dto.StudentHoldResult;

/**
 * Lists the current holds of a student with their section, ordered by section code, so that the student sees
 * what is held and what is confirmed.
 */
public interface ListStudentHoldsUseCase {

    List<StudentHoldResult> listStudentHolds(UUID studentId);
}
