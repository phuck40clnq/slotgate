# Event-Driven Architecture

## Table of contents
- [Scope](#scope)
- [Purpose](#purpose)
- [Event Philosophy](#event-philosophy)
- [Event Ownership](#event-ownership)
- [Event Structure](#event-structure)
- [Delivery Model](#delivery-model)
- [Failure Handling Principles](#failure-handling-principles)
- [Consistency Model](#consistency-model)
- [Architectural Constraints](#architectural-constraints)

---

## Scope

This document defines the architectural rules and constraints for asynchronous communication between services.

It complements [ADR-0001](./../adr/0001-use-event-driven-architecture.md) and the [Event Contract Policy](./../../contracts/avro/README.md).

---

## Purpose

The event-driven model enables:
- Loose coupling between services
- Independent deployments
- Asynchronous state propagation
- Horizontal scalability
- Clear domain ownership

Events are the primary integration mechanism between services.

---

## Event Philosophy

Events represent **facts that already happened** in a service’s domain.

An event:

- Is immutable
- Describes a completed state change
- Is published only by the service that owns the domain
- Can be consumed by zero or more subscribers

Events are not commands.  
They do not request actions.  
They communicate facts.

---

## Event Ownership

Each service:

- Publishes events only for its own domain
- Remains the source of truth for its data
- Must not modify another service’s data directly

Cross-service workflows emerge through event propagation,
not direct service invocation.

---

## Event Structure

Event structure is defined and enforced via Avro schema contracts, follow a consistent structure:
- Event ID (unique identifier)
- Event Type
- Occurred Timestamp
- Fields attributes (domain data)

&rarr; See: [`contracts/avro/README.md`](./../../contracts/avro/README.md) for more information

Event payloads must:
- Contain only necessary domain information
- Avoid leaking internal data structures
- Remain backward compatible across versions

---

## Delivery Model

The architecture assumes:
- At-least-once delivery
- Events may be delivered more than once
- Global ordering is not guaranteed

&rarr; Therefore:
- Consumers must be idempotent
- Event handling must tolerate duplicates
- Processing must not rely on strict ordering

---

## Failure Handling Principles

Services consuming events must:
- Handle temporary failures safely
- Avoid partial state corruption
- Remain resilient to reprocessing

Retry policies and dead-letter handling are infrastructure concerns, but services must tolerate repeated delivery.

---

## Consistency Model

The system operates under **eventual consistency**.

State changes propagate asynchronously across services.

Services must not assume immediate consistency between domains.

---

## Architectural Constraints

- Events are immutable facts
- Event contracts are part of the public integration layer
- Consumers must not depend on publisher internals
- Services remain loosely coupled
- No synchronous fallback for failed event flows
