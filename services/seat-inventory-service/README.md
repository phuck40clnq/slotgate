# Seat Inventory Service

Owns sections and their seats, and guarantees that a section is never oversold. This is the strongly consistent core of the hot path in the [PRD](../../docs/prd-2026-10-17.pdf).

The data is simulated. This service is not used for real registration at any university.

## API

The contract is [seat-inventory.openapi.yaml](../../contracts/openapi/seat-inventory/seat-inventory.openapi.yaml). Through the gateway, the prefix is `/api/seat-inventory` instead of `/api/v1/seat-inventory`.

| Method and path | Purpose | Errors |
|---|---|---|
| `POST /api/v1/seat-inventory/sections` | Open a section with a capacity | `400 INVALID_REQUEST` |
| `GET /api/v1/seat-inventory/sections/{sectionId}` | Read a section and the seats left | `404 SECTION_NOT_FOUND` |
| `POST /api/v1/seat-inventory/sections/{sectionId}/holds` | Hold one seat for the student in `X-Internal-User-Id` until `expiresAt`: `201` for a new hold, `200` with the existing hold when the student asks again | `404 SECTION_NOT_FOUND`, `409 SECTION_FULL` |
| `POST /api/v1/seat-inventory/sections/{sectionId}/holds/{holdId}/confirmation` | Confirm the student's hold before it expires, so the seat stays theirs: `200`, also when confirmed again | `404 HOLD_NOT_FOUND`, `409 HOLD_EXPIRED` |

Health and metrics are at `/actuator/health` and `/actuator/metrics`.

## How overselling is prevented

A claim is one conditional `UPDATE`, in `SeatInventoryPersistenceAdapter`. The reasoning and the measurements are in [ADR-0005](../../docs/adr/0005-claim-seats-with-an-atomic-conditional-update.md).

## How repeated requests are handled

A student has at most one held or confirmed seat per section. The hold is saved before the seat is claimed, and a unique `(section_id, student_id)` over active holds turns a double click or a retry into the existing hold instead of a second seat, in `SeatHoldPersistenceAdapter`. The reasoning and the measurements are in [ADR-0006](../../docs/adr/0006-make-seat-holds-idempotent-per-student-and-section.md).

## How holds end

A hold lasts `SEAT_HOLD_TTL` (5 minutes by default). The student confirms it before then, or a sweep every `SEAT_HOLD_SWEEP_INTERVAL` (5 seconds by default) expires it and gives the seat back, one section per transaction. Confirmation and expiry are both conditional updates on `status = 'HELD'`, so a hold ends one way only. `SEAT_HOLD_SWEEP_ENABLED=false` stops an instance from sweeping. The reasoning and the measurements are in [ADR-0007](../../docs/adr/0007-expire-unconfirmed-seat-holds-with-a-periodic-sweep.md).

## Run locally

Follow [Getting started](../../README.md#getting-started) for the shared POMs and the database, then:

```bash
cd services/seat-inventory-service
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

The port is `8084`. The variables are listed in `.env.example`.

## Test

```bash
./mvnw verify
```

This needs no Docker and no running database: integration tests start their own PostgreSQL ([ADR-0004](../../docs/adr/0004-run-integration-tests-on-embedded-postgresql.md)).

To reproduce the oversell scenario with before and after numbers:

```bash
./mvnw verify -Dit.test=OversellScenario
```

To reproduce the double click scenario:

```bash
./mvnw verify -Dit.test=DoubleClickScenario
```

To reproduce the phantom hold scenario, with and without the sweep, and the race between a confirmation and an expiry:

```bash
./mvnw verify -Dit.test=PhantomHoldScenario
./mvnw verify -Dit.test=ConfirmExpiryRaceScenario
```

## Not built yet

No event is published, so nothing outside this service learns that a hold was confirmed or expired.
