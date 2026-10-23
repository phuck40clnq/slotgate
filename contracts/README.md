# Contracts

## Table of contents
- [Overview](#overview)
- [Purpose](#purpose)
- [Structure](#structure)
- [Avro – Event Contracts](#avro---event-contracts)
- [OpenAPI – REST Contracts](#openapi--rest-contracts)
- [Contract Governance](#contract-governance)
- [Contracts First](#contracts-first)
- [Authoritative Contracts](#contracts-are-authoritative)
- [Backward Compatibility](#backward-compatibility)
- [When to Update Contracts](#when-to-update-contracts)
- [More Information](#more-information)

---

## Overview

The `contracts` module defines all cross-service communication contracts in the system.

This module acts as the single source of truth for:
- Service-to-service communication
- Gateway-to-service REST contracts
- Event-driven messaging schemas

No service should define its own public contract outside this module.

---

## Purpose

**This module ensures**:
- Strong schema versioning
- Explicit ownership of API definitions
- Independent service evolution
- Safe refactoring
- Clear boundary definitions
- Stable integration across services

Contracts define system boundaries.

Implementations must comply with them.

---

## Structure
```code
contracts
├── README.md
├── avro/      # Event schemas
└── openapi/   # HTTP API contracts
```

---

### Avro - Event Contracts

Contains **event contracts for asynchronous communication between services**.

These contracts define the structure of messages exchanged through the event-driven system.

They describe:
- The shape of domain events
- The data exposed to other services
- The public event interface of a service

This layer represents the asynchronous boundary of the system.

Services use these contracts when:
- Publishing domain events
- Consuming events from other services
- Reacting to state changes across bounded contexts

**Avro contracts ensure**:
- Schema consistency
- Controlled evolution of events
- Safe inter-service messaging
- Compatibility enforcement

### OpenAPI – REST Contracts

Contains **HTTP API contracts for synchronous communication**.

These contracts define the structure of REST requests and responses exposed by services.

They describe:
- Public endpoints
- Request/response formats
- Shared error structures
- Standard headers (when applicable)

This layer represents the synchronous boundary of the system.

Services use these contracts when:
- Exposing APIs through the Gateway
- Communicating over HTTP
- Integrating with external clients

OpenAPI contracts ensure:
- Predictable service interfaces
- Clear API ownership
- Safe evolution of REST endpoints

---

## Contract Governance

### Contracts First
Before implementing a feature that crosses service boundaries:
1. Define the OpenAPI or Avro schema
2. Review structure and ownership
3. Validate compatibility impact

Implementation must follow contract — not the other way around.

---

### Contracts are authoritative.
Changes to:
- `services/*`
- `frontend/*`
- `infrastructure/messaging`
- Event producers or consumers

must update the relevant contract definitions.

If behavior changes externally, contracts must reflect it.

---

### Backward Compatibility
#### OpenAPI
- Do not remove fields without versioning
- Do not change response shape incompatibly
- Avoid changing status code semantics
- Prefer additive changes

#### Avro
- Follow schema evolution best practices
- Prefer adding optional fields
- Avoid removing required fields
- Maintain compatibility strategy

#### Breaking changes require:
- Explicit versioning
- Migration strategy
- Documentation update
- Consumer impact analysis

---

## When to Update Contracts
Contracts must be updated when:
- A new API endpoint is introduced
- A response model changes
- An event schema is added
- An event payload structure changes
- Header requirements are modified
- Error response structure evolves

*Contracts are part of the change — not an afterthought.*

---

## More Information
For detailed documentation and architectural reasoning, see:

- [docs/architecture/overview.md](./../docs/architecture/overview.md)
- [docs/architecture/event-driven.md](./../docs/architecture/event-driven.md)
- [docs/rules/governance.md](./../docs/rules/governance.md)

Contracts define **what is exposed**.
Documentation explains **why and how**.
