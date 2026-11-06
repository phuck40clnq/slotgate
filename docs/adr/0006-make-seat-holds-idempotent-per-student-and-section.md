# ADR-0006: Make Seat Holds Idempotent per Student and Section

## Status
Accepted

## Context

The PRD requires exactly one final outcome per request, including retries, and lists the failure scenario "a student clicks twice". [ADR-0005](./0005-claim-seats-with-an-atomic-conditional-update.md) made sure a section is never oversold, but every hold request still claimed a new seat: nothing looked at who was asking.

The scenario was reproduced on commit `c3688cd` with one student and a section of 10 seats (`DoubleClickScenario`, one run):

| Case | Requests accepted | Seats held by the student | Distinct hold IDs |
|---|---|---|---|
| Retry after a lost response, sequential | 2 | 2 | 2 |
| Two parallel requests | 2 | 2 | 2 |
| Twenty parallel requests | 10, the other 10 rejected as full | 10 | 10 |

One student emptied the section. The seat counter and the holds still agreed, so this is not overselling: the seats went to the wrong owner.

Claiming first also gives a wrong answer to a retry on the last seat. The student gets the seat, the response is lost, the retry finds the section full and receives `409 SECTION_FULL` while holding the seat. The existing API test `refusesHoldWhenSectionIsFull` asserted exactly this by holding twice as the same student.

A unique constraint alone does not fix it. Reading the code, the second request would claim a seat, fail on the constraint, roll back, and reach the generic handler as `500`. This was not run.

## Decision

A student holds at most one seat per section. The pair `(section_id, student_id)` is unique in `seat_hold` and serves as the idempotency key. A client-supplied `Idempotency-Key` header is not used: two clicks can carry two keys, while the business rule already names the duplicate.

The hold is saved before the seat is claimed, in one transaction:

```sql
INSERT INTO seat_hold (id, section_id, student_id, held_at)
SELECT :id, id, :studentId, :heldAt FROM section WHERE id = :sectionId
ON CONFLICT (section_id, student_id) DO NOTHING
```

- One row inserted: the seat is claimed with the conditional update of ADR-0005. A full section rolls the transaction back, hold included. The response is `201`.
- No row inserted: the existing hold is read and returned with `200`, without claiming. No hold found means the section does not exist, which is `404 SECTION_NOT_FOUND`.

Under READ COMMITTED, a second insert of the same pair waits on the unique index until the first transaction commits or rolls back, then skips or inserts. Only one request of a student reaches the claim. Every transaction inserts its hold before it locks the section row, and a request waiting on a duplicate holds no lock that another transaction needs, so these transactions do not deadlock. The foreign key check takes a `KEY SHARE` lock on the section row, which does not conflict with the claim's update.

Alternatives considered:
- Check for an existing hold, then claim: read then write again, with the race of ADR-0005.
- Claim first, then insert with `ON CONFLICT` and give the seat back: still answers `409` to a retry on the last seat.
- A unique constraint without changing the flow: answers `500` to the repeated request.
- Check that the section exists before inserting: one more query on every request. Selecting from `section` in the insert costs nothing on the common path.

## Consequences

Positive:
- `DoubleClickScenario` after the change, three runs with the same result: every request accepted, one seat held, one hold ID, in all three cases. `ReserveSeatConcurrencyIT` checks this on every build with 2 and 200 parallel requests.
- A retry on the last seat returns the hold instead of `409`.
- `OversellScenario` (200 callers, 10 seats, atomic update), three runs each on the same machine and day:

| Commit | p50 | p99 | Seats held |
|---|---|---|---|
| `c3688cd`, claim first | 49 to 67 ms | 108 to 138 ms | 10, 10, 10 |
| This change, hold first | 7 to 11 ms | 107 to 186 ms | 10, 10, 10 |

  The median dropped and p99 did not improve. One explanation, not verified: the accepted transactions now hold the section row lock from the update to the commit only, no longer across the hold insert. The caveat of ADR-0005 applies: an embedded server with `fsync=off`, useful only to compare these runs.

Negative:
- Once a section is full, every rejected request inserts a hold and rolls it back. That is extra write-ahead log and dead rows in `seat_hold`, not yet measured on a durable server.
- Between different students, the order is the order of claims in the database, not of clicks: a student on a faster network can win. Fair ordering belongs to the gate and waiting room, not to the seat inventory.
- Hold expiry, not built yet, must keep this rule: either the constraint covers active holds only, or expired holds are removed. Otherwise a student whose hold expired can never hold that section again.
- A student can still hold seats in several sections. Credit caps belong to the per-university configuration.
