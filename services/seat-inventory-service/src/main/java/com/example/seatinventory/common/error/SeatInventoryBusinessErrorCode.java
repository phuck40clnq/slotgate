package com.example.seatinventory.common.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum SeatInventoryBusinessErrorCode {

    SECTION_NOT_FOUND("Section does not exist", HttpStatus.NOT_FOUND),
    SECTION_FULL("Section has no seats left", HttpStatus.CONFLICT);

    private final String message;
    private final HttpStatus status;

    SeatInventoryBusinessErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
