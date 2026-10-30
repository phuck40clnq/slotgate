# ADR-0005: Claim Seats with an Atomic Conditional Update

## Status
Accepted

## Context

The first goal of the PRD is never to oversell a section, also when many students ask for the last seats at the same moment. Holds are the hot path: at opening, nearly all requests target a few sections.

The obvious implementation reads the seat count, checks it, and writes the new count. The PRD scenario "two students compete for the last seat" was reproduced against it on PostgreSQL, with 200 parallel callers on a section of 10 seats (`OversellScenario`, three runs):

| Claim | Accepted | Seats held | `reserved_seats` at the end | p99 latency |
|---|---|---|---|---|
| Read, check, write | 199, 199, 199 | 199, 199, 199 | 4, 7, 8 | 320 to 519 ms |
| Atomic conditional update | 10, 10, 10 | 10, 10, 10 | 10, 10, 10 | 98 to 163 ms |

The read-then-write version oversold the section almost twentyfold. Its counter also stayed below the capacity: concurrent callers overwrote each other's increment, so the system still believed seats were left. A `CHECK` constraint on the counter cannot catch this, because the counter never exceeds the capacity.

Latencies come from an embedded server with `fsync=off` and only compare these two runs. The two versions also do different work, as the correct one rejects most callers without writing a hold.

## Decision

A seat is claimed by one statement that both checks and increments:

```sql
UPDATE section
SET reserved_seats = reserved_seats + 1
WHERE id = :id AND reserved_seats < capacity
```

One updated row means the seat is claimed. Under READ COMMITTED, concurrent claims queue on the row lock, and PostgreSQL re-evaluates the `WHERE` clause on the latest committed row once the lock is released. The hold is written in the same transaction, so a failed hold gives the seat back.

`CHECK (reserved_seats BETWEEN 0 AND capacity)` stays as a second line of defence against any other code path.

The service uses plain JDBC, not JPA, so that this statement stays visible in the code.

Alternatives considered:
- `SELECT ... FOR UPDATE` then write: also correct, with one more round trip while holding the lock.
- Optimistic locking with a version column: under contention on one row most attempts fail and retry, which is exactly the load this path must absorb.
- Redis as the seat counter: faster, but adds a second store that must agree with the holds in PostgreSQL. Split only on evidence.

## Consequences

Positive:
- Overselling is impossible as long as seats are claimed through this statement, which `ReserveSeatConcurrencyIT` proves on every build.
- One statement and one constraint are simple to reason about.

Negative:
- All claims of one section serialise on one row, so a section's throughput is limited by the time a transaction holds the lock. The transaction must stay short and MUST NOT call a remote system.
- Whether this meets the PRD target (p99 under 300 ms at peak) is not known yet. It needs a load test on a durable server. If the target is missed, this decision is revisited with those numbers.
