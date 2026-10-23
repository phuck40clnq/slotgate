# Test Service

## Table of contents
- [Overview](#overview)
- [Purpose](#purpose)
- [Runtime Context](#runtime-context)
- [Access Model](#access-model)
- [Scope](#scope)
- [Configuration](#configuration)
- [How to run local](#how-to-run-local)
    - [Requirements](#requirements)
    - [Run the service](#run-the-service)
- [Why This Service Exists](#why-this-service-exists)

---

## Overview

Test Service is a **lightweight internal service** used to validate infrastructure behavior within the platform.

It exists purely for **development** and **verification purposes**.

It does not implement business logic.

---

## Purpose

This service is used to:
- Verify Gateway routing
- Validate identity propagation
- Inspect injected headers
- Test CORS behavior
- Confirm environment setup
- Inspect structured logging output

---

## Runtime Context

Test Service runs behind the Gateway and relies on upstream authentication and identity propagation.

It:
- Uses a simple REST adapter (TestController)
- Relies on environment-based configuration (local, dev, prod)
- Does not contain domain or business logic

---

## Access Model

Test Service is exposed through the Gateway.

Depending on configuration, it may:
- Be configured as a public (permit-all) route
- Be protected to validate identity propagation

This allows developers to test:
- Authenticated vs unauthenticated routes
- Header injection behavior
- Request forwarding rules

---

## Scope

Test Service:
- Exposes simple HTTP endpoints
- Returns diagnostic responses
- Logs request metadata

It does **not**:
- Persist data
- Own domain models
- Participate in event-driven workflows
- Integrate with Kafka
- Implement business logic

---

## Configuration
Environment-specific configuration:
- `application.yml`
- `application-local.yml`
- `application-dev.yml`
- `application-prod.yml`

No external infrastructure dependencies are required.

---

## How to run (local)

### Requirements
- Java 21

---

### Run the service
From project root:

#### macOS / Linux
```bash
cd services/test-service
./mvnw spring-boot:run
```

#### Windows (PowerShell / CMD)
```PowerShell
cd services/test-service
mvnw.cmd spring-boot:run
```

---

## Why This Service Exists

This service is intentionally minimal.

It acts as:
- A verification target
- A debugging aid
- A development utility
- A safe sandbox for infrastructure testing

It helps validate routing, security boundaries, and logging behavior before integrating real domain services.
