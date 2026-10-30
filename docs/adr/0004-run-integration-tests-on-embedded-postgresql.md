# ADR-0004: Run Integration Tests on Embedded PostgreSQL

## Status
Accepted

## Context

The testing standard requires integration tests against the real technology, started by the test itself, with one command and no manual setup.

The usual tool for this is Testcontainers, which needs a container runtime. On macOS that means a Linux virtual machine. The development machine has 4 threads and 8 GB of RAM, and a virtual machine with 2 CPUs and 2 GB holds half of those threads for as long as it runs.

A test that needs a PostgreSQL server already running on the host would avoid the virtual machine, but it would need manual setup, and its result would depend on the state and version of that server.

H2 is not an option: the concurrency tests exist to prove how PostgreSQL behaves under load, and H2 locks differently.

## Decision

Integration tests run on a real PostgreSQL server started from Maven-managed binaries with `io.zonky.test:embedded-postgres`, in a throwaway data directory. The PostgreSQL release is pinned in `java-bom`.

Docker remains the choice for CI, deployment, production and end-to-end runs. Local development uses a PostgreSQL server installed on the host.

## Consequences

Positive:
- `./mvnw verify` needs no Docker and no prepared database.
- Every run starts from an empty server on one pinned release, on a developer machine and in CI.
- Tests exercise real PostgreSQL locking.

Negative:
- A server started this way runs with `fsync=off`. Tests prove correctness; their timings are not comparable with production.
- The release in tests (18.6) can differ from the one a developer runs locally.
- Kafka is not covered by this decision. It needs its own choice when the first Kafka adapter test is written.
- The dependency downloads binaries for every platform, about 130 MB once per machine.
