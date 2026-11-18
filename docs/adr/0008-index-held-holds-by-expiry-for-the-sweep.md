# ADR-0008: Index Held Holds by Expiry for the Sweep

## Status
Accepted

## Context

The expiry sweep of [ADR-0007](./0007-expire-unconfirmed-seat-holds-with-a-periodic-sweep.md) finds the sections with expired holds by reading every `HELD` hold, since no index covered `expires_at`. ADR-0007 left a hypothesis to measure: under load, this scan makes the p99 of new holds jump at every sweep, like a stop-the-world garbage collector. The prediction written before the run was a clear jump of more than 100 ms at each sweep.

`SweepLoadScenario` preloads 100,000 holds over 1,000 sections: 70 % confirmed, 25 % expired, and 5 % held with a hold period ending during the run, so that every sweep has work. After a 5 s warm-up, 32 callers send new holds to 20 busy sections for 30 s while a sweep runs every 2 s. The p99 of new holds is recorded per second, with the seconds of a sweep marked. One run per variant on one laptop, the index variant second, on the embedded server, which runs with `fsync` off.

| | No index | Partial index on `expires_at` |
|---|---|---|
| Sweep query | Parallel Seq Scan, 396 ms on average, 511 ms at most | Index Scan, 4.6 ms on average, 12 ms at most |
| Sweeps in 30 s | 4, the longest 7.4 s | 8, the longest 3.3 s |
| p99 of new holds: all / seconds with a sweep / other seconds | 194 / 191 / 205 ms | 145 / 144 / 150 ms |
| New holds | 71,901 | 97,420 |

A first run without warm-up gave the same picture: 274 ms against 4.7 ms for the sweep query, and a p99 of 187 ms against 167 ms.

The prediction was wrong: the p99 of the seconds with a sweep is no higher than that of the other seconds, so the scan causes no spike. Yet without the index the whole run is slower. Two findings were not predicted:
- A sweep takes seconds, much longer than its query, because each per-section transaction waits for a connection of the pool that the 32 callers keep busy. A seat therefore comes back several sweep intervals after its hold expired, not within one as ADR-0007 assumed.
- The index raises throughput by a third and lowers p99 by 50 ms. One explanation, not verified: the workers of the parallel scan compete for the CPU of the laptop during most of the run, since the sweeps last most of it.

## Decision

Migration V5 adds a partial index on `seat_hold (expires_at) WHERE status = 'HELD'`. The sweep query reads only the held holds that expired.

Alternatives considered:
- Keep the scan: costs 400 ms per sweep at 100,000 holds and grows with the table, for no benefit.
- An index on `(status, expires_at)`: also covers the confirmed and expired holds, which the sweep never looks for, so it is larger for the same query.

## Consequences

Positive:
- The sweep query takes milliseconds and stops growing with the number of confirmed and expired holds.
- In this run, sweeps kept closer to their interval and new holds were faster and more numerous.

Negative:
- Every new hold writes one more index entry, and so does every confirmation or expiry that removes it from the index. Not measured on its own; throughput rose overall.
- `CREATE INDEX` blocks writes to `seat_hold` while it builds. Harmless on the tables of today; a large table would need `CREATE INDEX CONCURRENTLY` outside a transaction.
- Under load a sweep still takes seconds, because it shares the connection pool with the requests. The return of a seat is late by several sweep intervals; ADR-0007 is wrong on that point. Not addressed here.
- Why the index improved p99 is not proven, and each variant ran once, in a fixed order, with `fsync` off. The numbers only compare the two variants.
