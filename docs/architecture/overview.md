# System Architecture Overview

## Table of contents
- [Architectural Style](#architectural-style)
- [System Components](#system-components)
- [Interaction Model](#interaction-model)
    - [Synchronous Communication](#synchronous-communication)
    - [Asynchronous Communication](#asynchronous-communication)
    - [Rules](#rules)
- [Service Boundaries](#service-boundaries)
- [Data Ownership Model](#data-ownership-model)
- [Scalability and Evolution](#scalability-and-evolution)
- [Architectural Constraints](#architectural-constraints)

---

## Architectural Style

The system follows an **Microservices architecture with Event-Driven integration**.

Primary goals:
- Independent service deployment
- Clear domain boundaries
- Loose coupling between services
- Horizontal scalability
- Contract-driven communication

*Synchronous communication* is used when immediate responses are required.

*Asynchronous messaging* is the primary integration mechanism between services.

---

## System Components

The system consists of:

- **API Gateway**: Single entry point for clients.
- **Authentication Service**: Manages identity and access control.
- **Domain Services**: Encapsulate business logic and domain ownership.
- **Message Infrastructure**: Enables asynchronous communication between services.
- **Service-Owned Data Stores** (per service): Each service owns its data store.

Each component has a clearly defined responsibility and ownership boundary.

---

## Interaction Model

Two communication patterns are supported:

### Synchronous Communication

Client &rarr; Gateway &rarr; Service

Used for:
- Queries
- Immediate response requirements

---

### Asynchronous Communication

Service &rarr; Publish Event &rarr; Message Infrastructure &rarr; Subscriber Services

Used for:
- State propagation
- Cross-service reactions
- Long-running workflows

---

### Rules

- No shared database
- No direct cross-service data access
- All communication happens through APIs or events

---

## Service Boundaries

Each service:
- Owns its domain logic
- Owns its database
- Defines its public API contract
- Publishes domain events on state changes
- Consumes only relevant events 

Cross-service communication must **only** occur via:
- HTTP APIs (synchronous queries or commands)
- Events (asynchronous state propagation)

---

## Data Ownership Model

The architecture enforces strict data ownership:
- No shared persistence layer
- No cross-service joins
- No direct database integration

The system operates under an **eventual consistency** model.

Each service is the single source of truth for its domain.

---

## Scalability and Evolution

The architecture supports:
- Independent service scaling
- Independent deployment cycles
- Multi-language service implementation
- Infrastructure evolution with minimal systemic impact
- Gradual adoption of advanced patterns (e.g. Saga, CQRS)

Stateless services are preferred.

State is stored exclusively within owned data stores.

---

## Architectural Constraints

The system follows these architectural constraints:

- Public APIs are contract-driven
- Events are shared and immutable
- Event contracts are centrally governed and version-controlled
- Breaking changes require formal architectural decisions
- Backward compatibility is preferred
