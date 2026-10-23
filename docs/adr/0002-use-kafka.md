# ADR-0002: Use Kafka as Event Broker

## Status
Accepted

## Context

Following [ADR-0001](./0001-use-event-driven-architecture.md) (Use Event-Driven Architecture),
the system requires an event broker to support asynchronous communication.

The broker must support:
- Asynchronous communication
- High throughput
- Durable event storage
- Consumer groups
- Scalability

The broker must support decoupled producers and consumers and handle increasing load over time.

## Decision

The system will use Apache Kafka as the event broker.

Kafka will be responsible for:
- Event persistence
- Topic-based event distribution
- Consumer group coordination
- Horizontal scalability

Services interact with Kafka through well-defined event contracts.

## Consequences

### Positive

- High throughput and scalability
- Durable event log
- Strong ecosystem and tooling
- Supports replay and event-driven workflows

### Negative

- Operational complexity
- Requires infrastructure management
- Requires monitoring and observability
