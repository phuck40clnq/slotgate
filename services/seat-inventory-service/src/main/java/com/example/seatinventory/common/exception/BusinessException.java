package com.example.seatinventory.common.exception;

import java.util.Map;

import com.example.seatinventory.common.error.SeatInventoryBusinessErrorCode;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final SeatInventoryBusinessErrorCode errorCode;
    private final Map<String, Object> params;

    public BusinessException(SeatInventoryBusinessErrorCode errorCode, Map<String, Object> params) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.params = Map.copyOf(params);
    }
}
