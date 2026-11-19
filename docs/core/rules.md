# Core rules

The invariants no change to Slot Gate may break, binding every contributor. Rules use the prefix `CORE`, and changing one follows DOC-57.

## Guarantees

| ID | Rule | Check |
|---|---|---|
| CORE-01 | A section MUST NOT have more held and confirmed seats than its capacity, under any concurrency. | Scenario test |
| CORE-02 | A registration request MUST reach exactly one final outcome, also when retried or delivered twice. | Scenario test |
| CORE-03 | An expired hold MUST give its seat back to the section. | Scenario test |
| CORE-04 | Load on one tenant MUST NOT push another tenant's p99 past its target in [product](product.md). | Load test |
| CORE-05 | A slow or failed school system MUST NOT block or lose an accepted registration. | Scenario test |
| CORE-06 | Every outcome MUST record the rule and configuration version behind it. | Review |
| CORE-07 | Data MUST be simulated; real personal data MUST NOT enter the repository, databases, logs or tests. | Review |

## Services

| ID | Rule | Check |
|---|---|---|
| CORE-08 | Services MUST NOT share code, a database or a domain model, and MUST communicate only through `contracts/`. | Review |
| CORE-09 | A contract MUST change before its implementation, which MUST match it exactly. | Review |
| CORE-10 | Each piece of data MUST have one owning service; others MAY hold only projections of its events. | Review |
| CORE-11 | Every API, event and stored record MUST carry its tenant, and a request MUST NOT reach another tenant's data. | Review |
| CORE-12 | The gateway MUST be the only public entry, and services MUST trust identity only from its headers. | Review |
| CORE-13 | An error MUST carry a stable code in the error format of `contracts/`, without internal details. | Review |
| CORE-14 | The hot path MUST NOT call a school system or evaluate a rule the cold path can compute. | Review |
| CORE-15 | A service MAY use any language with an accepted ADR, a platform baseline and a service template. | Review |
| CORE-16 | A new service MUST be created only when a measured target is violated or an ADR finds an unowned boundary. | Review |
| CORE-17 | Dependencies inside a service MUST point inward to the domain, which MUST NOT import a framework. | Architecture test |

## Events

| ID | Rule | Check |
|---|---|---|
| CORE-18 | An event MUST be an immutable fact, named in the past tense, with `eventId`, `occurredAt` and its tenant. | Review |
| CORE-19 | A consumer MUST be idempotent by `eventId`, since delivery is at least once, and assume order only per partition key. | Test |
| CORE-20 | An event schema MUST stay compatible with its consumers; a breaking change MUST be a new event version. | Review |

## Operations

| ID | Rule | Check |
|---|---|---|
| CORE-21 | Every service MUST log JSON to standard output, propagate trace context over HTTP and events, and expose health and metrics. | Review |
| CORE-22 | Configuration MUST come from environment variables listed in `.env.example`, and secrets MUST NOT be committed. | Review |
| CORE-23 | Every service MUST ship a container image definition and a `README.md`. | Review |
| CORE-24 | A claimed performance or reliability gain MUST show before and after numbers from a reproducible test. | Review |
