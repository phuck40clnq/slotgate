# API Gateway

## Table of contents
- [Scope](#scope)
- [Purpose](#purpose)
- [Responsibilities](#responsibilities)
- [What the Gateway MUST NOT do](#what-the-gateway-must-not-do)
- [Interaction Model](#interaction-model)
- [Security Boundary](#security-boundary)
- [Stateless Design](#stateless-design)
- [Scalability Model](#scalability-model)
- [Architectural Constraints](#architectural-constraints)

---

## Scope

This document defines the architectural role, responsibilities, and constraints of the API Gateway.

It does not describe implementation details.

---

## Purpose

The API Gateway is the single entry point for all external client requests.

It provides centralized control over request routing, authentication delegation,
and cross-cutting concerns while keeping business logic inside domain services.

The Gateway must remain lightweight and stateless.

---

## Responsibilities

The Gateway is responsible for:
- Routing external requests to appropriate services
- Validating authentication tokens
- Forwarding identity context to downstream services
- Handling rate limiting and throttling
- Managing cross-origin policies
- Observability (logging, tracing, metrics)

The Gateway may transform transport-level concerns, but must not alter domain semantics.

---

## What the Gateway MUST NOT do

The Gateway must **never**:
- Contain domain business logic
- Access service databases directly
- Perform cross-service orchestration
- Maintain business state
- Call multiple services to compose domain workflows

*Business workflows **must** remain inside services or be event-driven*.

---

## Interaction Model

Client &rarr; Gateway &rarr; Service

Authentication flow:

Client &rarr; Gateway &rarr; Authentication Service (for login / token issuance)

For authenticated requests:
- Gateway validates the token.
- Gateway forwards validated identity context to downstream services.
- Services perform domain-level authorization independently.

**Rules**:
- External authentication must be validated at the Gateway boundary.
- Downstream services must perform domain-level authorization based on the identity context.
- Services communicate with each other directly via APIs or events — never through the Gateway.

---

## Security Boundary

The Gateway represents the system’s external trust boundary.

It ensures that:

- Only authenticated requests enter the system
- Public endpoints are controlled and monitored
- Malformed or malicious requests are rejected early

Internal services are not directly exposed to the public network.

---

## Stateless Design

The Gateway must be stateless.

- No session storage
- No business state
- No in-memory domain cache

State must be stored in services that own it.

Stateless design ensures horizontal scalability.

---

## Scalability Model
The Gateway is designed to scale independently from domain services.

It supports:
- Horizontal scaling
- Independent deployment
- Graceful degradation under high load
- Load balancing at the edge

Gateway scaling must not require domain changes.

---

## Architectural Constraints

- The Gateway must remain thin
- No domain coupling
- No business workflow logic
- No shared persistence
- No cross-service orchestration
- Must not centralize domain authorization policies

The Gateway is an infrastructure component, not a domain service.
