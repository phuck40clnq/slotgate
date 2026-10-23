# User Service

User Service manages user domain data and profile lifecycle within the platform.

It consumes authentication events and maintains its own user projection independently.

---

## Table of contents
- [Overview](#overview)
- [Responsibilities](#responsibilities)
- [Architecture](#architecture)
	- [Adapter layer](#adapter-layer)
	- [Application layer](#application-layer)
	- [Domain layer](#domain-layer)
	- [Infrastructure layer](#infrastructure-layer)
- [Security Model](#security-model)
- [Event Integration](#event-integration)
- [Contracts](#contracts)
- [Configuration](#configuration)
- [How to run local](#how-to-run-local)
	- [Requirements](#requirements)
	- [Run the service](#run-the-service)
- [Deployment](#deployment)
- [Future Improvements](#future-improvements)

---

## Overview

User Service is built using Clean Architecture (Ports & Adapters).

It is responsible for maintaining user domain data independently from the authentication layer.

This service:
- Consumes user-related domain events from Apache Kafka
- Creates and maintains user profile projections
- Exposes user-related APIs
- Publishes user domain events when necessary

User Service does **not** handle authentication or token issuance.

---

## Responsibilities

- Create user profile from authentication events
- Maintain user profile data
- Handle profile updates
- Expose user-related APIs
- Publish user domain events (if required)

*User Service owns the user domain model but **not** authentication credentials*.

---

## Architecture
The service follows a strict separation of concerns.

### Adapter Layer
- REST controllers
- Messaging handlers

### Application Layer
- Use cases
- Command / Result DTOs
- Input and Output ports

### Domain Layer
- UserProfile aggregate
- UserStatus model
- Domain events


### Infrastructure Layer
- JPA persistence
- Kafka integration
- Event translators and mappers

All external integrations are accessed through ports to preserve domain isolation and testability.

---

## Security Model
- Identity validation is performed upstream by the API Gateway.
- User Service operates on already-validated identity context.
- Authorization rules, if introduced, belong to the application layer.

User Service does not issue or validate JWT tokens directly.

---

## Event Integration
User Service consumes authentication events from Kafka using Avro-based contracts:
- [`UserRegistered`](./src/main/resources/contracts/avro/UserRegistered.avsc) published from Auth Service

Event flow:

Example: `UserRegistered`
1. Auth Service publishes UserRegistered
2. User Service consumes the event
3. A user profile projection is created internally

Events:
- Are defined through Avro schema contracts
- Follow contract-first governance
- Are translated into domain commands
- Handled via an explicit event dispatcher registry

This ensures loose coupling and eventual consistency between bounded contexts.

---

## Contracts
The service aligns with platform-level contracts:
- OpenAPI (HTTP layer)
- Avro (Event layer)

Contracts are treated as the canonical schema layer of the system and define inter-service communication boundaries.

---

## Configuration
Environment-specific configurations provided via:
- [`application-local.yml`](./src/main/resources/application-local.yml)
- [`application-dev.yml`](./src/main/resources/application-dev.yml)
- [`application-prod.yml`](./src/main/resources/application-prod.yml)

    And [`application.yml`](./src/main/resources/application.yml)

Primary configuration areas include:
- Database connection
- Kafka connectivity
- Logging configuration

---

## How to run (local)

### Requirements
- Java 21

> Make sure infrastructure (Database, Apache Kafka, etc.) is already running.
>
> See platform setup guide in the [root README](./../../README.md#getting-started):

---

### Run the service
From project root:

#### macOS / Linux
```bash
cd services/user-service
./mvnw spring-boot:run
```

#### Windows (PowerShell / CMD)
```PowerShell
cd services/user-service
mvnw.cmd spring-boot:run
```

---

## Deployment
- Independently deployable

---

## Future Improvements
- Additional user lifecycle events
- Soft-delete / state transitions
- Audit logging support
- Role / permission projection (if required)
