# ADR-0007: Expire Unconfirmed Seat Holds with a Periodic Sweep

## Status
Accepted

## Context

The PRD lists "holds with an expiry and a confirmation" as the second MVP feature, and the failure scenario "phantom holds" must prove that nobody keeps a seat forever. Until this change a hold never ended: nothing decremented `reserved_seats`, so a student who held a seat and left kept it.

`PhantomHoldScenario` reproduces this with 10 seats, 10 holds that are never confirmed, and a late student. Without a sweep, which stands in for the code before this change, the late student was turned away for the whole 3 s observed and the section showed 0 seats left. The earlier commit itself was not run.

The PRD puts the hold time in the per-university configuration, which does not exist yet. No public regulation gives a value for it.

## Decision

A hold is `HELD` until the student confirms it (`CONFIRMED`) or its hold period ends and a sweep expires it (`EXPIRED`). `seat_hold` gains `status` and `expires_at`. Only `HELD` and `CONFIRMED` holds count against the section.

**Hold period.** `expires_at = held_at + SEAT_HOLD_TTL`, 5 minutes by default. This is an assumption: confirming is one click, and the value moves to the per-university configuration later.

**Confirmation** is one conditional update that touches the hold row only, never the section row, so it does not queue on a busy section:

```sql
UPDATE seat_hold SET status = 'CONFIRMED'
WHERE id = :id AND status = 'HELD' AND expires_at > :now
```

If it changes nothing, the hold is read again. An earlier or concurrent confirmation left it `CONFIRMED`, which returns `200` with the hold; anything else is `409 HOLD_EXPIRED`. A hold of another student or another section is `404 HOLD_NOT_FOUND`.

**Expiry** is a sweep every `SEAT_HOLD_SWEEP_INTERVAL`, 5 s by default (also an assumption). It lists the sections with expired holds, then handles each section in its own transaction:

```sql
UPDATE seat_hold SET status = 'EXPIRED'
WHERE section_id = :sectionId AND status = 'HELD' AND expires_at <= :now;
UPDATE section SET reserved_seats = reserved_seats - :expiredHolds WHERE id = :sectionId;
```

One section per transaction keeps the row lock of a busy section short and lets several instances sweep without deadlocking each other. Hold rows are locked before the section row, the same order as a new hold. Confirmation and expiry use the primitive of [ADR-0005](./0005-claim-seats-with-an-atomic-conditional-update.md): when both target the same hold, the second waits for the row lock, PostgreSQL re-evaluates `status = 'HELD'` on the committed row, and it changes nothing. A hold ends confirmed or expired, never both. `SEAT_HOLD_SWEEP_ENABLED=false` stops an instance from sweeping.

**Uniqueness** now covers active holds only, the option [ADR-0006](./0006-make-seat-holds-idempotent-per-student-and-section.md) left open for expiry. The unique constraint becomes a partial unique index on `(section_id, student_id) WHERE status IN ('HELD', 'CONFIRMED')`, and `ON CONFLICT` repeats that predicate so PostgreSQL picks the index. A student can hold a section again after a hold expired. Expired rows stay for the decision log of the PRD.

**Holding again while the sweep runs.** If the insert skips because of an active hold and the sweep expires that hold before it is read, nothing is found. The request then tries once more: only an expired hold lets the second insert succeed, so a missing section still answers `404 SECTION_NOT_FOUND`.

**Time** comes from the service's injected clock and is passed into the SQL, not from `now()` in the database, so there is one time source and tests control it.

Alternatives considered:
- Reclaim an expired hold when a claim finds the section full: no job, and the seat comes back exactly at expiry, but it adds work to the claim path when contention is highest, and `GET` shows a wrong `seatsLeft` until somebody claims.
- Count active holds instead of keeping a counter: reverses ADR-0005.
- Delete expired holds: keeps the full unique constraint but loses the history.
- Read the status in Java, then write by id: measured below as check-then-write.

## Consequences

Positive:
- `PhantomHoldScenario` with a 1 s hold period and a sweep every 500 ms, one run: the first seat came back 331 ms after the first hold expired, and all 10 seats 89 ms after the last one expired. Without a sweep, 0 seats came back in 3 s.
- `ConfirmExpiryRaceScenario`: 200 holds, confirmations sent 1 s before the hold period ends race one sweep run 1 s after it, so every hold qualifies for both. The sweep starts once half of the confirmations have returned. Three runs per version:

| Version | Run | Told confirmed / expired | Confirmed / expired in the database | Reserved seats | Confirmed without a seat | Correct |
|---|---|---|---|---|---|---|
| check-then-write | 1 | 200 / 0 | 200 / 0 | 200 | 0 | yes |
| check-then-write | 2 | 199 / 1 | 197 / 3 | 194 | 3 | no |
| check-then-write | 3 | 200 / 0 | 145 / 55 | 144 | 1 | no |
| conditional update | 1 | 198 / 2 | 198 / 2 | 198 | 0 | yes |
| conditional update | 2 | 200 / 0 | 200 / 0 | 200 | 0 | yes |
| conditional update | 3 | 200 / 0 | 200 / 0 | 200 | 0 | yes |

  Check-then-write fails in two ways. The sweep overwrites a hold that was just confirmed, so a student told "confirmed" has lost the seat (55 students in run 3). Or a confirmation overwrites an expired hold whose seat the sweep already gave back, so a confirmed hold has no seat and the section can be filled past what it really has (1 to 3 holds per run). How much breaks depends on timing: 0 to 56 holds per run.
- A first version of the scenario started the sweep together with the confirmations. Check-then-write broke in all three runs (19, 200 and 200 holds wrong). The conditional version was correct in all three, but each run was all or nothing (200 expired, 200 expired, 200 confirmed), so it showed little about the race. Hence the start at half. Even so, only one conditional run interleaved at the row level (198 / 2).
- `ExpireSeatHoldsConcurrencyIT` checks on every build that the race leaves every hold confirmed or expired, the seat count equal to the confirmed holds, and every caller told the truth.

Negative:
- A seat comes back up to one sweep interval after its hold expires.
- The sweep reads every `HELD` hold, because no index covers `expires_at`. Hypothesis to measure next: under load this scan makes the p99 of new holds jump at every sweep, like a stop-the-world garbage collector. The next round runs about 100,000 holds and continuous claims, records p99 per second, then adds a partial index on `expires_at WHERE status = 'HELD'` and compares.
- Holds placed in the first minute of opening all expire about 5 minutes later, which frees many seats at once and starts a second wave of contention. Not measured.
- For up to one sweep interval after a hold expires, a retry of the same student returns that hold, already past `expiresAt`, instead of a new one.
- Every instance sweeps by default. That is safe but repeats work; `SEAT_HOLD_SWEEP_ENABLED` can limit it to one instance.
- Expiry follows the clock of the instance that sweeps, so clock skew between instances shifts it.
- A client can let a hold expire and hold again in a loop. Rate limiting at the gateway, as the PRD plans, has to stop that.
- No event is published when a hold expires, so the waitlist cannot react yet.
- Being partial, the unique index no longer serves lookups by section that do not filter on active statuses, as migration V2 relied on. No current query needs one.
- The numbers come from an embedded PostgreSQL on one laptop, with the caveat of ADR-0005: useful only to compare these runs.
