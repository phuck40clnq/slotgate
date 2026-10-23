# Gateway Service

Gateway Service is the platform's external entry point and security boundary.

It is responsible for request routing, JWT validation, identity propagation, and cross-cutting infrastructure concerns across services.

All external HTTP traffic flow through the Gateway.

---

## Table of contents
- [Overview](#overview)
- [Responsibilities](#responsibilities)
- [Architecture](#architecture)
    - [Configuration Layer](#configuration-layer)
    - [Filter Layer](#filter-layer)
    - [Error Handling Layer](#error-handling-layer)
- [Security Model](#security-model)
    - [Token Validation](#token-validation)
    - [Identity Context](#identity-context)
- [Routing & Identity Propagation](#routing--identity-propagation)
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

Gateway Service is build on Spring Cloud Gateway.

It acts as:
- The single API entry point
- The JWT validation layer
- The identity propagation layer
- The centralized cross-cutting policy component

The Gateway does **not** contain business logic or domain models.

---

## Responsibilities

- Validate JWT tokens using JWKS
- Enforce authentication requirements
- Route requests to downstream services
- Propagate validated identity context downstream
- Normalize and standardize error responses
- Apply cross-origin (CORS) configuration

*Gateway is designed to remain stateless and horizontally scalable*.

---

## Architecture
Gateway follows a layered edge architecture focused on security and cross-cuting concerns.

### Configuration Layer
- Security configuration
- CORS configuration
- Identity configuration
- Route definitions (via configuration files)

This layer defines the **system boundary rules**.

### Filter Layer
Custom filters handle:
- Identity propagation
- Route-based identity skipping (for public endpoints)
- Header management

Filters are explicitly ordered to **guarantee deterministic execution**.

### Error Handling Layer
- Centralized error resolver and handler
- Typed error mappers
- Unified error response writer

All framework, validation, and network errors are normalized before being returned to clients.

&rarr; This ensures consistent API behavior across services.

---

## Security Model

### Token Validation
- JWT tokens are validated using public keys exposed by Auth Service (via JWKS)
- Signature, expiration, issuer, and audience are verified
- Invalid or missing tokens are rejected before routing

The Gateway acts as the *system’s authentication enforcement boundary*.

### Identity Context
After successful validation:
- Identity claims (e.g., sub) are extracted
- Internal headers are injected into downstream requests
- Downstream services operate on validated identity context

Downstream services rely on the validated identity context and do **not** re-validate tokens.

---

## Routing & Identity Propagation
Gateway:
- Forwards validated requests to target services
- Injects identity headers when required
- Allows configured public routes to bypass authentication

It does **not** transform or interpret business payloads.

---

## Contracts
Gateway respects platform-level contracts:
- OpenAPI (HTTP boundary)
- JWT identity contract (issuer and audience alignment)

It does not own domain contracts or event schemas.

---

## Configuration
Environment-specific configuration:
- [`application.yml`](./src/main/resources/application.yml)
- [`application-local.yml`](./src/main/resources/application-local.yml)

Primary configuration areas:
- Route definitions
- JWKS endpoint location
- JWT validation rules
- CORS settings

---

## How to Run (Local)

### Requirements
- Java 21
- Auth Service running (for JWKS endpoint)
- Downstream services running

---

### Run the Service

#### macOS / Linux

```bash
cd services/gateway-service
./mvnw spring-boot:run
```

#### Windows

```PowerShell
cd services/gateway-service
mvnw.cmd spring-boot:run
```

---

## Deployment
- Independently deployable
- Stateless
- Horizontally scalable

---

## Future Improvements
- Rate limiting support
- Dynamic route configuration
- Observability improvements (metrics / tracing)
- Advanced policy enforcement

---

## More Information

### Documentation about JWT
- [Decision of using JWT](../../docs/adr/0003-use-jwt-rsa.md)
- [Architecture of authentication](../../docs/architecture/authentication.md)
