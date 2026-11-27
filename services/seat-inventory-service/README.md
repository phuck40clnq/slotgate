# seat-inventory-service

Owns sections and their seats, and guarantees that a section is never oversold. For anyone who changes holds, confirmation or expiry, or calls them.

## Why it exists

Holding and confirming seats is the strongly consistent core of the hot path in the [product requirements](../../docs/prd-2026-10-17.pdf), and CORE-01 must hold under any concurrency, so one service owns every seat.

## API and events

The contract is [seat-inventory.openapi.yaml](../../contracts/openapi/seat-inventory/seat-inventory.openapi.yaml), with the statuses and error codes of every operation. Through the gateway, the prefix is `/api/seat-inventory` instead of `/api/v1/seat-inventory`.

| Method and path | Purpose |
|---|---|
| `POST /api/v1/seat-inventory/sections` | Open a section with a unique code, a title and a capacity |
| `GET /api/v1/seat-inventory/sections` | List every section with the seats left |
| `GET /api/v1/seat-inventory/sections/{sectionId}` | Read a section and the seats left |
| `POST /api/v1/seat-inventory/sections/{sectionId}/holds` | Hold one seat for the student in `X-Internal-User-Id` |
| `POST /api/v1/seat-inventory/sections/{sectionId}/holds/{holdId}/confirmation` | Confirm the student's hold before it expires |
| `GET /api/v1/seat-inventory/me/holds` | List the current holds of the student in `X-Internal-User-Id` |

Health and metrics are at `/actuator/health` and `/actuator/metrics`. The service publishes no event, so nothing outside it learns that a hold was confirmed or expired.

## Configuration

The variables are listed in `.env.example`.

| Variable | Default | Meaning |
|---|---|---|
| `PORT` | `8084` | The HTTP port |
| `SERVICE_NAME` | `seat-inventory-service` | The application name |
| `SPRING_PROFILES_ACTIVE` | None | `local` connects to the PostgreSQL server on the host |
| `DB_NAME`, `DB_USER`, `DB_PASS` | `slotgate_seat_inventory` | The database, role and password of the `local` profile, on `localhost:5432` |
| `SEAT_HOLD_TTL` | `PT5M` | How long a student has to confirm a hold |
| `SEAT_HOLD_SWEEP_INTERVAL` | `PT5S` | The pause between two sweeps that give the seats of expired holds back |
| `SEAT_HOLD_SWEEP_ENABLED` | `true` | `false` stops this instance from sweeping |

## Run

Follow [Getting started](../../README.md#getting-started) for the shared build files and the database, then:

```bash
cd services/seat-inventory-service
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

To open the simulated sections of [simulated-sections.json](scripts/simulated-sections.json) in the running service, run the script below. It needs `curl` and `jq`, and skips a section whose code exists, so it is safe to run again:

```bash
services/seat-inventory-service/scripts/seed-simulated-sections.sh
```

`SEAT_INVENTORY_URL` points it at another instance than `http://localhost:8084`.

## Test

```bash
cd services/seat-inventory-service
./mvnw verify
```

This needs no Docker and no running database: integration tests start their own PostgreSQL ([ADR-0004](../../docs/adr/0004-run-integration-tests-on-embedded-postgresql.md)).

Each scenario runs on demand and prints its before and after numbers:

```bash
cd services/seat-inventory-service
./mvnw verify -Dit.test=OversellScenario
./mvnw verify -Dit.test=DoubleClickScenario
./mvnw verify -Dit.test=PhantomHoldScenario
./mvnw verify -Dit.test=ConfirmExpiryRaceScenario
./mvnw verify -Dit.test=SweepLoadScenario
```

| Scenario | What it reproduces | Duration |
|---|---|---|
| `OversellScenario` | Overselling under concurrent holds | About 30 s |
| `DoubleClickScenario` | A double click or a retry of the same hold | About 30 s |
| `PhantomHoldScenario` | Holds that are never confirmed, with and without the sweep | About 35 s |
| `ConfirmExpiryRaceScenario` | A confirmation racing the expiry of the same hold | About 35 s |
| `SweepLoadScenario` | The sweep under load, without and with the index on expiry; it empties the tables of its own embedded database | About 2 min |

Each duration is one run of the whole command, build included, on the development laptop on 2026-11-27.

## Details

### How overselling is prevented

A claim is one conditional `UPDATE`, in `SeatInventoryPersistenceAdapter`. The reasoning and the measurements are in [ADR-0005](../../docs/adr/0005-claim-seats-with-an-atomic-conditional-update.md).

### How repeated requests are handled

A student has at most one held or confirmed seat per section. The hold is saved before the seat is claimed. In `SeatHoldPersistenceAdapter`, a unique `(section_id, student_id)` over active holds turns a double click or a retry into the existing hold, not a second seat. The reasoning and the measurements are in [ADR-0006](../../docs/adr/0006-make-seat-holds-idempotent-per-student-and-section.md).

### How holds end

A hold lasts `SEAT_HOLD_TTL`. The student confirms it before then, or a sweep every `SEAT_HOLD_SWEEP_INTERVAL` expires it and gives the seat back, one section per transaction. Confirmation and expiry are both conditional updates on `status = 'HELD'`, so a hold ends one way only. The reasoning and the measurements are in [ADR-0007](../../docs/adr/0007-expire-unconfirmed-seat-holds-with-a-periodic-sweep.md).

The sweep finds expired holds through a partial index on the expiry of held holds. Under load a sweep still takes seconds, because it shares the connection pool with requests, so a seat comes back several intervals after its hold expired. The measurements are in [ADR-0008](../../docs/adr/0008-index-held-holds-by-expiry-for-the-sweep.md).
