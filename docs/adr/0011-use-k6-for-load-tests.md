# ADR-0011: Use k6 for load tests

## Status

Accepted

## Context

Slot Gate must hold up at the peak of registration, so its latency has to be measured under load. The first question is how much the gateway adds to holding a seat. The tool runs on a developer laptop with 8 GB of memory and no Docker for local work. It must report p50 and p99 for chosen requests apart from warm-up and setup, drive a fixed number of concurrent callers, and keep its scenarios as files in the repository.

The prediction was written before the first run, for about 10 students at once each holding one seat:
- straight to `seat-inventory-service`: p50 10 ms, p99 50 ms;
- through the gateway, about 20 % slower: p50 12 ms, p99 60 ms.

The first scenario, [`gateway-hold-latency.js`](../../infrastructure/load-tests/gateway-hold-latency.js), sends holds from 10 virtual users in a closed loop: each run has 200 warm-up holds, then 2,000 measured ones. Every hold uses a new (student, section) pair, so every request claims a seat instead of returning an existing hold ([ADR-0006](./0006-make-seat-holds-idempotent-per-student-and-section.md)). A run holds 100 students in 22 new sections of 100 seats, and all 13,200 holds of the six runs answered `201`.

Conditions:
- One laptop: Intel i5-7267U (2 cores, 4 threads), 8 GB, macOS.
- Java 21.0.8, PostgreSQL 14.20 on the host, k6 2.4.0.
- The services ran from `main` as jars with the `local` profile, on the same machine as k6.
- No build ran, and the editor used about 70 % of one thread.
- In each round the direct run went first; the load average was below 4 before rounds 1 and 3.

| Round | Straight to `seat-inventory-service` | Gateway, logging as on `main` | Gateway, logging at `INFO` |
|---|---|---|---|
| 1 | p50 10.2 ms, p99 93.0 ms, 533 holds/s | p50 33.5 ms, p99 159.8 ms, 186 holds/s | |
| 2 | p50 6.8 ms, p99 75.0 ms, 843 holds/s | p50 24.8 ms, p99 161.8 ms, 276 holds/s | |
| 3 | p50 6.0 ms, p99 48.1 ms, 902 holds/s | | p50 17.7 ms, p99 126.4 ms, 259 holds/s |

The prediction was right for the direct p50 and wrong for the gateway. Through the gateway, p50 was 2.9 to 3.7 times the direct p50 of the same round, not 1.2 times, and p99 was 126 to 162 ms instead of 60 ms. Two findings were not predicted:
- `main` logs `org.springframework.cloud.gateway` and `org.springframework.security` at `DEBUG`. The gateway wrote 158,523 `DEBUG` lines for 4,403 requests, about 36 per request, to the console and to a file. At `INFO` its p50 fell to 17.7 ms, still about 3 times the direct p50.
- Throughput through the gateway was a third of the direct one. One explanation, not verified: k6, three services, PostgreSQL and the editor share 4 threads, and the gateway adds a token check and a second HTTP hop to each hold.

## Options

- **k6:** one binary, scenarios in JavaScript, executors for closed and open loops, and tags with thresholds that report the measured requests apart. Chosen.
- **Gatling:** runs on the JVM and fits the Java build, but a simulation needs its own Maven or Gradle project, and a JVM load generator takes memory this laptop lacks while three services run.
- **Apache JMeter:** a graphical tool whose test plans are XML, hard to review in a pull request.
- **A JUnit scenario, as for [ADR-0008](./0008-index-held-holds-by-expiry-for-the-sweep.md):** measures one service from inside its tests, so it cannot send traffic through the gateway and the services as they run.

## Decision

Write load tests as k6 scripts in `infrastructure/load-tests/`, one script per scenario. Report p50 and p99 of the measured requests only, record the conditions next to each result, and write the prediction before the run.

## Consequences

Positive:
- A scenario is one short file that anyone with k6 can run against the services as they run, with or without the gateway.
- Warm-up, setup and measured requests are reported apart, so a result covers what it claims.

Negative:
- k6 sits outside the Maven build and the platform BOM; each machine installs it, and its version is pinned nowhere.
- Scripts are JavaScript run by k6, outside every build of the repository, so no build checks them.
- On this laptop k6 competes for the CPU with what it measures, so absolute numbers mix load generator and services. They compare routes within one round; they do not give the latency of a deployment.
- The gateway scenario needs one token per student, and the repository has no committed way to make them.
- The `DEBUG` logging of the gateway on `main` is reported, not changed here.
