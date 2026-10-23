# Documentation policy

## Table of contents
- [Overview](#overview)
- [Project Documents](#project-documents)
- [Architecture](#architecture)
- [Rules & Conventions](#rules--conventions)
- [Architecture Decision Records (ADR)](#architecture-decision-records-adr)
- [WHEN TO UPDATE](#when-to-update-documentation)
- [Documentation Standards](#documentation-standards)
    - [Documentation of changes](#documentation-as-part-of-change)
    - [Level of detail](#level-of-detail)
    - [Ownership](#ownership)
- [Reading Order](#reading-order-recommended)

---

## Overview

This directory contains **technical documentation** of the system.

Its purpose is to:
- Preserve architectural intent
- Document system behavior and design rules
- Record important architectural decisions
- Provide a single source of truth for engineers

Documentation is organized by topic to ensure clarity, maintainability, and long-term consistency.

---

## Project Documents

- [prd-2026-10-17.pdf](./prd-2026-10-17.pdf) - Product requirements (Vietnamese): goals, MVP scope, targets, roadmap
- [incidents/](./incidents/README.md) - Incident records and post-mortems, the raw material for root cause analysis

---

## Architecture

High-level and service-level architecture documents.

- [architecture/overview.md](./architecture/overview.md) - System overview and main components
- [architecture/gateway.md](./architecture/gateway.md) - API Gateway design and responsibilites
- [architecture/authentication.md](./architecture/authentication.md) - Authentication and security model
- [architecture/event-driven.md](./architecture/event-driven.md) - Event-driven communication

---

## Rules & Conventions

Shared rules and guidelines across services.

- [rules/governance.md](./rules/governance.md) - Architectural invariants
- [rules/coding.md](./rules/coding.md) - Coding standard for every language, including comments and technical debt
- [rules/java.md](./rules/java.md) - Java and Spring standard
- [rules/testing.md](./rules/testing.md) - Testing standard
- [CONTRIBUTING.md](../CONTRIBUTING.md) - Issues, branches, commits and pull requests

---

## Architecture Decision Records (ADR)

Located in `adr/`.
ADR documents are **immutable records of significant architectural decisions**.

- [adr/overview.md](./adr/overview.md)

---

## When to update documentation

You should update the documentation when:

- A public API or OpenAPI contract changes
- A Kafka event schema is added or modified
- A service introduces a new responsibility
- An architectural decision is made or changed

---

## Documentation Standards

### Documentation as part of change

Changes to:
- `contracts/`
- `services/*`
- `infrastructure/`

**must be accompanied by documentation updates**
when they affect behavior, contracts, or architecture.

---

### Level of detail

- README files explain **what and why**
- Detailed rules and flows live in dedicated documents
- Documentation should describe **intent**, not implementation details

---

### Ownership

Each document must clearly define:
- Its scope
- The layer or component it describes
- Its responsibility within the system

Documentation is a shared responsibility.

---

## Reading Order (Recommended)

1. [`architecture/overview.md`](./architecture/overview.md)
2. [`adr/overview.md`](./adr/overview.md)
3. Services [`README.md`](./../services/README.md) and service-level `README` files under `services/`
