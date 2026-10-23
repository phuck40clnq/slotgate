# Architecture Decision Records (ADR)

## Table of contents
- [Overview & Purpose](#overview--purpose)
- [Scope](#scope)
- [Lifecycle](#lifecycle)
- [Naming Convention](#naming-convention)
- [Format](#format)
- [Cross-Referencing](#cross-referencing)
- [Immutability Rule](#immutability-rule)
- [Governance](#governance)
- [Relationship to Documentation](#relationship-to-documentation)

---

## Overview & Purpose

This directory contains **Architecture Decision Records (ADRs)**.

An ADR captures a significant architectural decision, including its context, rationale, and consequences.

ADRs are the **source of truth for architectural intent**.

They preserve design reasoning and document how the system evolves over time.

Architecture is the result of accumulated decisions.

ADRs record those decisions explicitly.

---

## Scope

An ADR must be created for decisions that:
- Affect multiple services
- Introduce cross-cutting concerns
- Influence system-wide behavior
- Impact scalability, security, reliability, or maintainability
- Have long-term architectural implications

Minor refactoring or service-local implementation details do not require an ADR.

---

## Lifecycle

Each ADR has one of the following statuses:
- **Proposed** – Under discussion
- **Accepted** – Official decision
- **Deprecated** – No longer recommended
- **Superseded** – Replaced by a newer ADR

Only **Accepted** ADRs define the current architecture.

---

## Naming Convention

ADR files must follow the naming pattern:

`000X-short-kebab-case-title.md`

Example: [`0001-use-event-driven-architecture.md`](./0001-use-event-driven-architecture.md)

The numeric prefix defines chronological order and must not be reused.

---

## Format

Each ADR must follow this structure:

```markdown
# ADR-XXXX: Title

## Status
Proposed | Accepted | Deprecated | Superseded

## Context
Describe the problem, constraints, and forces influencing the decision.

## Decision
Clearly state what has been decided.

## Consequences
Explain trade-offs, risks, and impacts (positive and negative).
```

---

## Cross-Referencing

If an ADR impacts or replaces another ADR:
- The new ADR must reference to the previous one.
- The superseded ADR must reference to the replacing ADR.

This ensures traceability of architectural evolution.

---

## Immutability Rule

Once an ADR is **Accepted**, it must not be modified.

If the decision changes:
- Create a new ADR
- Mark the previous ADR as **Superseded**
- Reference the new ADR in the old one

Architecture evolves by adding decisions, not rewriting history.

---

## Governance

- Architects (or technical leads) are responsible for structural decisions.
- Engineers are responsible for proposing ADRs when necessary.
- Acceptance authority must be clearly defined within the team.

Architectural clarity is a shared responsibility.

---

## Relationship to Documentation

- ADRs document **why** decisions were made.
- Implementation details belong elsewhere.
- ADRs complement documentation; they do not replace it.
