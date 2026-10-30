package com.example.seatinventory.application.port.out;

import java.util.UUID;

/**
 * Creates identifiers, so that tests can control them.
 */
public interface IdGeneratorPort {

    UUID nextId();
}
