# Auth Service

Auth Service is the platform’s identity entry point.

It is responsible for authenticating users, issuing signed JWT access tokens, and publishing authentication domain events.

JWT verification is handled by the Gateway.

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
    - [Token type](#token-type)
    - [JWT Claims](#jwt-claims)
    - [Verification](#verification)
    - [Refresh token](#refresh-token)
- [Event Integration](#event-integration)
- [Contracts](#contracts)
- [Configuration](#configuration)
- [How to run local](#how-to-run-local)
    - [Requirements](#requirements)
    - [Run the service](#run-the-service)
- [Deployment](#deployment)
- [Future Improvements](#future-improvements)
- [More Information](#more-information)
    - [Documentation about JWT](#documentation-about-jwt)

---

## Overview

Auth Service is built using Clean Architecture (Ports & Adapters).

It acts as an internal JWT issuer within a contract-governed microservice system.

This service:
- Issues RSA-signed access tokens
- Exposes a JWKS endpoint for public key discovery
- Publishes authentication events to Apache Kafka
- Does **not** act as a full OAuth2 Authorization Server

It manages **authentication credentials only** — not full user domain data.

---

## Responsibilities

- User registration
- User login
- Password hashing and verification
- Access token issuance
- JWKS exposure
- Publishing authentication domain events

*Auth Service is **not** the source of truth for user profile data or authorization policies*.

---

## Architecture
The service follows a strict separation of concerns.

### Adapter Layer
- REST controllers
- System endpoints (e.g., JWKS)

### Application Layer
- Use cases
- Command / Result DTOs
- Input and Output ports

### Domain Layer
- Core models
- Domain events

### Infrastructure Layer
- JPA persistence
- Kafka integration
- JWT & RSA signing
- Password encoding

All external integrations are accessed through ports to preserve domain isolation and testability.

---

## Security Model

### Token Type
- Access token only
- RSA signed (RS256)
- Keys provided via PEM files
- Public key exposed through JWKS

The service acts strictly as a **token issuer**.

### JWT Claims
Access tokens contain minimal identity information:
- `iss`
- `sub` (userId)
- `aud`
- `iat`
- `exp`

No roles or scopes are embedded in the token.

### Verification
JWT verification is performed externally by Gateway using the JWKS endpoint.

### Refresh Token
Refresh token support is not implemented yet.

---

## Event Integration
Auth Service publishes authentication domain events to Kafka using Avro-based contracts:
- [`UserRegistered`](./src/main/resources/contracts/avro/UserRegistered.avsc)
- [`UserLoggedIn`](./src/main/resources/contracts/avro/UserLoggedIn.avsc)

Events:
- Are defined through Avro schema contracts
- Follow contract-first governance
- Are mapped from domain events via dedicated mappers
- Routed via explicit event router registry

Downstream services consume these events to build independent projections and maintain eventual consistency.

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
- JWT issuer and expiration
- RSA key locations

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
cd services/auth-service
./mvnw spring-boot:run
```

#### Windows (PowerShell / CMD)
```PowerShell
cd services/auth-service
mvnw.cmd spring-boot:run
```

---

## Deployment
- Independently deployable

---

## Future Improvements
- Refresh token support
- Key rotation strategy
- Token revocation mechanism
- Optional OAuth2 compatibility layer (if required)

---

## More Information

### Documentation about JWT
- [Decision of using JWT](../../docs/adr/0003-use-jwt-rsa.md)
- [Architecture of authentication](../../docs/architecture/authentication.md)
