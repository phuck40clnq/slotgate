# Seat Inventory Service

Owns sections and their seats, and guarantees that a section is never oversold. This is the strongly consistent core of the hot path in the [PRD](../../docs/prd-2026-10-17.pdf).

The data is simulated. This service is not used for real registration at any university.

## API

The contract is [seat-inventory.openapi.yaml](../../contracts/openapi/seat-inventory/seat-inventory.openapi.yaml). Through the gateway, the prefix is `/api/seat-inventory` instead of `/api/v1/seat-inventory`.

| Method and path | Purpose | Errors |
|---|---|---|
| `POST /api/v1/seat-inventory/sections` | Open a section with a capacity | `400 INVALID_REQUEST` |
| `GET /api/v1/seat-inventory/sections/{sectionId}` | Read a section and the seats left | `404 SECTION_NOT_FOUND` |
| `POST /api/v1/seat-inventory/sections/{sectionId}/holds` | Hold one seat for the student in `X-Internal-User-Id`: `201` for a new hold, `200` with the existing hold when the student asks again | `404 SECTION_NOT_FOUND`, `409 SECTION_FULL` |

Health and metrics are at `/actuator/health` and `/actuator/metrics`.

## How overselling is prevented

A claim is one conditional `UPDATE`, in `SeatInventoryPersistenceAdapter`. The reasoning and the measurements are in [ADR-0005](../../docs/adr/0005-claim-seats-with-an-atomic-conditional-update.md).

## How repeated requests are handled

A student holds at most one seat per section. The hold is saved before the seat is claimed, and a unique `(section_id, student_id)` turns a double click or a retry into the existing hold instead of a second seat, in `SeatHoldPersistenceAdapter`. The reasoning and the measurements are in [ADR-0006](../../docs/adr/0006-make-seat-holds-idempotent-per-student-and-section.md).

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

## Not built yet

A hold has no expiry or confirmation, and no event is published.
