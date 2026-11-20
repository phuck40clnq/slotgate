# Section list fails once it reads a section opened before codes existed

| Field | Value |
|---|---|
| Detected | 2026-11-14 21:02 UTC |
| Status | resolved |
| Severity | major: one bad row fails the whole list. It never left the branch |
| Services | seat-inventory-service |
| Commit | uncommitted work on `feat/list-sections`, based on `ca587b0` |
| Related | migration `V4__give_sections_a_code_and_a_title.sql` |

## Summary
`GET /api/v1/seat-inventory/sections` failed on the local database. Migration V4 gave each section opened before codes existed the code `'SIM-' || id`, 40 characters long, while `Section` accepts at most 32. The database stored it because `section.code` has no length constraint, so the failure only surfaced when a row was mapped back to `Section`. Every test passed, because the embedded database of the tests starts empty and no test runs V4 over existing rows.

## Prediction
Not a planned break. The expected outcome of the local run was a list of 12 simulated sections plus the 2 older ones.

## Timeline
| Time (UTC) | Event |
|---|---|
| 21:01 | Service started on the local database, at V3 with two sections; Flyway applied V4 |
| 21:01 | Seed script opened 12 simulated sections; a second run skipped all 12 as expected |
| 21:02 | `GET /sections` returned an error body instead of the list, and the service logged `ERROR Unexpected failure` |

## Symptoms
The listing came back without a `sections` field (`jq: Cannot iterate over null`). Opening sections and the seed script kept working, since neither reads the old rows.

## Evidence
```text
java.lang.IllegalArgumentException: code must have 1 to 32 characters: SIM-1a895b78-3d5b-4c30-a0c3-7648cd5d1dd6
	at com.example.seatinventory.domain.model.Section.requireText(Section.java:35)
	at com.example.seatinventory.domain.model.Section.<init>(Section.java:13)
	at com.example.seatinventory.infrastructure.persistence.SeatInventoryPersistenceAdapter.toSection(SeatInventoryPersistenceAdapter.java:93)
	at com.example.seatinventory.infrastructure.persistence.SeatInventoryPersistenceAdapter.lambda$listSections$1(SeatInventoryPersistenceAdapter.java:55)

$ psql -d slotgate_seat_inventory -c "select length(code), count(*) from section group by 1 order by 1"
 8|12
40|2
```

## Investigation
| # | Hypothesis | How it was tested | Result |
|---|---|---|---|
| 1 | A stored section breaks a rule of `Section` | Read the stack trace, then counted codes by length | Two codes of 40 characters, both written by the V4 backfill |
| 2 | The tests could not have seen it | Read `PostgresIntegrationTest`: every run starts an empty embedded server and migrates it | Confirmed: no test migrates a database that already holds rows |

## Root cause
One rule lived in two places that disagreed. `Section` limits a code to 32 characters; the `section.code` column accepts any text. A migration could therefore write rows that the service cannot read, and nothing checked a migration against existing data.

## Resolution
- V4 backfills `OLD-` followed by the first 28 hex digits of the id, 32 characters, and adds `CHECK` constraints that match `Section` for the code (1 to 32 characters) and the title (1 to 200). A wrong backfill now fails the migration at startup instead of every later read.
- New `MigrationIT` migrates a schema of its own to V3, inserts a section, migrates to the latest version and builds a `Section` from every row. On the first V4 it failed with the same `IllegalArgumentException`; on the corrected V4 it passes. `SeatInventoryPersistenceAdapterIT` checks that the database refuses a 33-character code.
- V4 had not left the branch, so it was corrected in place. The local database was rolled back to V3 by removing the 12 seeded sections (no holds), the two new columns and the V4 row of `flyway_schema_history`, then migrated again: 14 sections listed, the two older ones as `OLD-1a895b783d5b4c30a0c37648cd5d` and `OLD-5b22bedfa22f4334955fe20cd751`.

## Lessons
- A migration that writes data needs a test over existing rows. An empty test database only proves the schema.
- A limit enforced by the domain belongs in the database too; the Java standard says so for correctness invariants, and this length limit had been missed.
- Running the service on the local database caught what 91 green tests did not. Keep that step before calling a change done.
- One unreadable row fails the whole list. That was left as is: skipping bad rows would hide corrupt data instead of reporting it.
