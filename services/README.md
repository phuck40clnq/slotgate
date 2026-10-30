# Services

## Table of Contents
- [Overview](#overview)
- [Architectural Principles](#architectural-principles)
- [Special service](#special-purpose-services)
- [Documentation](#documentation)
    - [Domain services](#domain-services)
    - [Architecture & Decisions](#architecture--decisions)
- [Adding a New Service](#adding-a-new-service)
- [More Information](#more-information)

---

## Overview

The `services/` directory contains all deployable application services in the system.

Each service is an isolated runtime unit with its own lifecycle, configuration, and deployment process.

Services communicate only through well-defined contracts:
- **HTTP** (OpenAPI contracts)
- **Events** (Avro contracts)
- Shared platform standards

---

## Architectural Principles
- Services are independently deployable
- No shared business logic between services
- No direct code dependencies across services
- Communication strictly via published contracts
- Backward compatibility is mandatory

The goal is loose coupling and long-term evolvability.

---

## Special Purpose Services

Some services exist to support engineering workflows rather than business functionality.

`test-service`
The test-service is used for:
- Contract validation
- Integration testing
- Gateway routing verification
- Event publishing/consuming experiments
- Infrastructure debugging

It helps developers safely test:
- OpenAPI compatibility
- Event schema evolution
- Cross-service communication
- Error handling scenarios

It is not part of the production domain model but plays a critical role in development stability.

---

## Documentation

### Domain services

Each service contains its own detailed documentation.

To explore implementation details, API design, configuration, or runtime behavior:

-> See the `README.md` inside each service directory.

Examples:
- [services/auth-service/README.md](./auth-service/README.md)
- [services/gateway-service/README.md](./gateway-service/README.md)
- [services/user-service/README.md](./user-service/README.md)
- [services/seat-inventory-service/README.md](./seat-inventory-service/README.md)

---

### Architecture & Decisions

For system-level architecture and design decisions:
- [Architecture Overview](../docs/architecture/overview.md)
- [Architecture Decision Records (ADR)](../docs/adr/overview.md)

---

## Adding a New Service
When creating a new service:
1. Follow the existing service structure
1. Register OpenAPI contracts under `contracts/openapi`
1. Register event contracts under `contracts/avro`
1. Use platform parent/BOM
1. Provide a service-level README

---

## More Information
For broader system standards and governance:
- OpenAPI contract policies -> [contracts/openapi](./../contracts/openapi/README.md)
- Event schema governance -> [contracts/avro](./../contracts/avro/README.md)
- Platform standards -> [platform/](./../platform/README.md)
