# ERROR ARCHITECTURE
***This document defines the official error-handling architecture of the frontend application.***

### Table of contents:
- [Core principle](#1-core-principle)
- [Philosophy](#2-philosophy)
- [`AppError`](#3-Apperror-presentation--ui)
- [`BusinessError`](#4-businesserror-domain--application)
- [Boundary rule](#5-boundary-responsibility-critical-rule)
- [Global error boundary](#6-global-error-boundary-mainjs)
- [Forbidden](#7-anti-patterns-strictly-forbidden)
- [Why this works?](#8-why-this-architecture-scales)
---

Its **purpose** is to:
- preserve clean boundaries
- avoid UI ↔ domain coupling
- keep error handling scalable and maintainable long-term

### Summary error architecture:
The application **uses two parallel error hierarchies**, each with a single responsibility:

```
Error
 ├── BusinessError   (domain / application)
 └── AppError        (presentation / UI)
```

These hierarchies:
- **must never extend each other**
- **must never be mixed**
- are translated **only at explicit boundaries**
---

### 1. Core Principle

```
Domain errors and UI errors MUST be separated.
```

**Rationale**
#### The domain layer must be independent of:
- HTTP
- routing
- UI behavior
- presentation concerns

#### The UI layer must not depend on:
- domain invariants
- business rules
- internal domain semantics

*Violating this rule leads to*:
- tight coupling
- fragile code
- untestable domain logic
- poor long-term maintainability
---

### 2. Philosophy
1. HTTP never directly shows UI
1. Features never directly render raw errors
1. All errors pass through:
    - Normalizer
    - Registry
    - Handler
    - Presenter

This guarantees:
- Predictable behavior
- Debuggability
- Clear separation of responsibility

---

### 3. AppError (Presentation / UI)
**Definition**

`AppError` represents **how application reacts at the UI level**.

It should answer the question:
```
"How should the UI respond to this failure?"
```

#### Description:
An `AppError`:
- is consumed by the router / views
- defines presentation behavior
- may include redirect or retry policies

#### Responsibilities:
An `AppError` must responsible for:
- error page rendering
- redirects (e.g. to login)
- status codes (for UI logic)
- user-facing messages

**Examples**
``` javascript
UNAUTHORIZED
NOT_FOUND
NETWORK_ERROR
SERVICE_UNAVAILABLE
```
---

### 4. BusinessError (Domain / Application)
**Definition**

`BusinessError` represents **violations of business rules or domain invariants**.

It should answer the question:
```
"Why is this operation invalid in the business sense?"
```

#### Description:
A `BusinessError`:
- is thrown by **usecase / domain logic**
- has **no knowledge of UI or transport**
- contains **only domain-relevant data**

#### Must NOT contain:
A `BusinessError` must **never** contain:
- HTTP status code
- redirect logic
- user-facing messages
- UI-related metadata

**Examples**
``` javascript
class UserError extends BusinessError {}
```
---

### 5. Boundary Responsibility (Critical Rule)
```
Errors are translated ONLY at boundaries.
```

**What is a boundary?**

A boundary is a place where control crosses layers, for example:
- domain → UI
- infrastructure → application
- API → frontend

***Rule***
- **BusinessError may be thrown freely inside the domain**
- **AppError must never be thrown from the domain**
- Translation is allowed **only at the boundary**

Example

Domain (use case)
``` javascript
if (user.status !== "ACTIVE") {
  throw new UserNotActiveError({ userId: user.id });
}
```

- **Domain-only**
- **No UI knowledge**

#### Boundary (controller / router)
``` javascript
catch (err) {
  if (err instanceof UserNotActiveError) {
    throw new AppError({
      type: ERROR_TYPES.FORBIDDEN,
      message: "User is not active",
    });
  }

  throw err;
}
```

- **Explicit translation**
- **One-directional dependency**
---

### 6. Global Error Boundary (main.js)
#### The router acts as the **single global error boundary**.

**Responsibilities**:
- catch unhandled errors
- normalize them into AppError
- decide:
  - redirect
  - render error view
  - block navigation

This ensures:
- consistent error behavior
- no duplicated error handling in views
- predictable UI flow

---

### 7. Anti-Patterns (Strictly Forbidden)
The following are **explicitly disallowed**:
- `BusinessError extends AppError`
- `AppError extends BusinessError`
- throwing `AppError` from use cases
- handling errors directly inside components
- routing logic inside domain code

Violations will result in:
- architectural drift
- unmaintainable code
- hidden coupling
---

### 8. Why This Architecture Scales
#### This design allows:
- domain reuse (web, mobile, other clients)
- independent evolution of UI policies
- predictable error handling
- easier testing
- long-term maintainability
---
