# System Governance

## Table of contents
- [Overview](#overview)
- [Service Isolation](#service-isolation)
- [Contract Authority](#contract-authority)
- [Event Evolution](#event-evolution)
- [Dependency Direction](#dependency-direction)
- [Error Model Consistency](#error-model-consistency)
- [Data Ownership](#data-ownership)
- [Change Control](#change-control)
- [CFinal Notes](#final-notes)

---

## Overview

This document defines the architectural invariants of the system.

These rules protect long-term consistency and stability.

Any change affecting these invariants requires an approved ADR.

---

## Service Isolation

Each service is an independent deployable and autonomous unit.

**Rules**:
- Each service owns its database.
- Direct database access across services is strictly prohibited.
- Domain models MUST NOT be shared across services.
- Cross-service communication MUST occur only through:
    - HTTP APIs (synchronous)
    - Events (asynchronous)

Service boundaries MUST remain explicit and enforceable.

&rarr; See also:
- [docs/architecture/overview.md](./../architecture/overview.md)
- [docs/architecture/event-driven.md](./../architecture/event-driven.md)
- [services/README.md](./../../services/README.md)

---

## Contract Authority

All external communication is contract-driven.

**Rules**:
- `contracts/` is the single source of truth.
- API schemas MUST align with OpenAPI specifications.
- Event payloads MUST align with Avro schemas.
- Services MUST NOT redefine or override external payload structures internally.

*Contracts define the public surface of the system and MUST remain authoritative*.

&rarr; See also:
- [contracts/openapi/README.md](./../../contracts/openapi/README.md)
- [contracts/avro/README.md](./../../contracts/avro/README.md)

---

## Event Evolution

Events MUST evolve without breaking consumers.

**Rules**:
- Breaking changes MUST NOT occur without explicit versioning.
- Removing fields is considered a breaking change.
- New fields MUST preserve backward compatibility.
- Version changes MUST be explicit and intentional.

These principles are aligned with distributed event streaming systems.

&rarr; See also:
- [docs/architecture/event-driven.md](./../architecture/event-driven.md)

---

## Dependency Direction

Dependencies MUST flow inward.

**Rules**:
- Domain MUST NOT depend on infrastructure.
- Application defines ports.
- Infrastructure implements ports.
- Framework-specific code MUST remain in outer layers.

This enforces the principles of *Hexagonal Architecture*.

&rarr; See also:
- [docs/architecture/overview.md](./../architecture/overview.md)

---

## Error Model Consistency

Error handling MUST be predictable and uniform across services.

**Rules**:
- Business errors are distinct from infrastructure errors.
- Each error MUST have a stable, machine-readable error code.
- Error response structure MUST remain consistent system-wide.
- Gateway MUST NOT expose internal implementation details.

Failure behavior is part of the public contract and MUST remain stable.

&rarr; See also:
- [contracts/openapi/error/README.md](./../../contracts/openapi/error/README.md)

---

## Data Ownership

Each aggregate has a single authoritative owner.

**Rules**:
- Write authority MUST NOT be duplicated across services.
- Derived data MUST be treated as projections.
- Events communicate state transitions, not database representations.

Data ownership MUST remain unambiguous.

&rarr; See also:
- [docs/architecture/event-driven.md](./../architecture/event-driven.md)

---

## Change Control

Any modification affecting:
- Service boundaries
- Contract structure
- Event compatibility
- Dependency direction
- Error model

requires an ADR before implementation.

&rarr; See also:
- [docs/architecture/overview.md](./../architecture/overview.md)

---

## Final Notes

- Architecture governance rules are intentionally stable and rarely modified.

- These constraints ensure the system remain evolvable, predictable, and maintainable as it grows.
