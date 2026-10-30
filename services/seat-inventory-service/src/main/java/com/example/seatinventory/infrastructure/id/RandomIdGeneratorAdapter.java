package com.example.seatinventory.infrastructure.id;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.seatinventory.application.port.out.IdGeneratorPort;

@Component
public class RandomIdGeneratorAdapter implements IdGeneratorPort {

    @Override
    public UUID nextId() {
        return UUID.randomUUID();
    }
}
