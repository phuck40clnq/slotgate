# Authentication Architecture

## Table of contents
- [Scope](#scope)
- [Purpose](#purpose)
- [Core Principles](#core-principles)
- [Authentication Flow](#authentication-flow)
- [Token Model](#token-model)
- [Identity Propagation](#identity-propagation)
- [Separation from Authorization](#separation-from-authorization)
- [Expiration and Revocation](#expiration-and-revocation)
- [Security Constraints](#security-constraints)
- [Architectural Constraints](#architectural-constraints)

---

## Scope

This document defines the authentication architecture of the system.

It describes how identity is:
- Established
- Verified
- Issued
- Validated
- Propagated

*It does not define domain authorization rules*.

---

## Purpose

Authentication is responsible for verifying the identity of external actors.

The goals are to:
- Establish trusted identity
- Prevent unauthorized system access
- Provide a consistent identity model across services
- Maintain replaceable and scalable identity infrastructure

Authentication is a cross-cutting infrastructure concern.

*It is not a domain service*.

---

## Core Principles

- Identity verification happens at the system boundary.
- Authentication is logically centralized.
- Identity context must be minimal and standardized.
- Authentication mechanisms must be replaceable without domain impact.
- Authentication must not carry domain semantics.
- Authentication must not introduce domain coupling.

Any deviation from these principles requires explicit architectural review.

---

## Authentication Flow

1. Client submits credentials to the Authentication Service.
2. Authentication Service validates credentials.
3. A signed identity token is issued.
4. Client includes the token in subsequent requests.
5. The Gateway validates the token before forwarding the request.

Authentication is completed before a request reaches any domain service.

*Domain services **MUST NEVER** process raw credentials*.

---

## Token Model

Authentication issues signed identity tokens.

Token requirements **MUST**:
- Be cryptographically verifiable.
- Contain minimal identity claims.
- Include an explicit expiration time.
- Be portable across services.
- **NOT** require domain service coordination for validation.

Token validation in services **MUST NOT** require database access.

Token structure changes MUST preserve backward compatibility during transition periods.

Authentication tokens represent identity only.

*They **MUST NOT** encode business permissions or workflow state*.

---

## Identity Propagation

After validation at the Gateway:
- A verified identity context is forwarded to downstream services.
- The integrity of this context MUST be preserved.
- Services MUST rely only on verified identity context provided through trusted infrastructure.
- Services MUST NOT trust raw client credentials.

Internal communication MUST occur within a trusted network boundary.

Identity propagation MUST remain consistent across all services.

*No service may redefine the identity structure independently*.

---

## Separation from Authorization

Authentication verifies **who the actor is**.

Authorization determines **what the actor is allowed to do**.

Authorization rules:
- Belong to domain services.
- MUST NOT be centralized in the Authentication Service.
- MUST NOT be embedded in the Gateway.
- MUST NOT be encoded inside identity tokens.

This separation ensures:
- Domain autonomy
- Independent service evolution
- Scalable system growth

*Authentication provides identity*.

*Domains enforce permissions*.

---

## Expiration and Revocation

Tokens **MUST** have explicit expiration policies.

Short-lived tokens are preferred over centralized session state.

Authentication **MUST** support revocation mechanisms when required.

Revocation strategies **MUST**:
- NOT require domain service coordination.
- NOT introduce tight coupling.
- Avoid introducing centralized runtime dependency for every request.
- Avoid shared session state that limits scalability.

&rarr; Expired or revoked tokens **MUST** be rejected at the boundary.

*Authentication **MUST NOT** become a centralized session store*.

---

## Security Constraints

- Credentials MUST NEVER reach domain services.
- Tokens MUST be tamper-resistant and verifiable.
- Expired or invalid tokens MUST be rejected before reaching domain logic.
- Authentication failure handling MUST NOT leak sensitive system information.
- Internal services MUST NOT expose authentication endpoints publicly.
- Key management MUST support rotation without system-wide disruption.

*Authentication **MUST NOT** introduce domain coupling or operational fragility*.

---

## Architectural Constraints

Authentication:
- **MUST** remain stateless by default.
- **MUST NOT** own domain data.
- **MUST NOT** implement business workflow logic.
- **MUST NOT** enforce domain-specific permission models.
- **MUST NOT** become a centralized authorization engine.
- **MUST NOT** create tight coupling between services.

Authentication is an infrastructure component.

*It establishes trust and does NOT enforce business meaning*.
