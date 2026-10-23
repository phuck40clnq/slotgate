# Web Frontend

## Table of contents
- [Overview](#overview)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [JavaScript Architecture](#javascript-architecture)
  - [app/ – Application Layer](#1-app---application-layer)
  - [core/ – Infrastructure Layer](#2-core---infrastructure-layer)
  - [features/ – Feature Modules](#3-features--feature-modules)
  - [shared/ – Cross-Feature Reusables](#4-shared--cross-feature-reusables)
- [How to Run](#how-to-run)
- [How To Add a New Feature](#how-to-add-a-new-feature)
- [Design Principles](#design-principles)
- [More Information](#more-information)

---

## Overview

This is the **Vanilla JavaScript Web Client** of the microservices system.

It is:
- Framework-free (no React / Vue / Angular)
- Feature-based modular structure
- Explicit routing system
- Centralized error architecture
- Clear separation between:
    - core
    - app
    - shared
    - features

The frontend:
- Communicate exclusively through the **Gateway Service**
- Strictly follows backend contracts defined in:
    - [contracts/openapi](./../../contracts/openapi/README.md)

---

## Project Structure

``` text
frontend/web
├── README.md
├── assets          # Static assets (images, etc.)
├── css             # Global styles
├── index.html      # SPA entry
├── js              # Entire JS application
│   ├── app         # Application bootstrap & global error orchestration
│   ├── core        # Framework-like internal engine (router, http, session)
│   ├── features    # Business features (auth, user, dashboard, etc.)
│   ├── main.js     # Entry point
│   └── shared      # Reusable components, API clients, error base classes
└── jsconfig.json   # IDE support
```

### Architecture

The frontend follows a **layered + feature-oriented architecture**.

```code
main.js -> app/ -> core/ -> features/ -> shared/
```

---

## JavaScript Architecture

### 1. `app/` - Application layer
Responsible for bootstrapping and global error flow.

**Important Files**:
- `bootstrap.js` - Initializes application
- `bootstrapSession.js` - Restores session on startup
- `app.js` - App runtine
- `error/` - Global app-level error orchestration

*App Error Flow*:
```text
Error -> Decider -> Executor -> Presenter -> UI
```

---

### 2. `core/` - Infrastructure layer
Reusable application-level infrastructure.

- **Router**: `router.js`, `route.model.js`, `route.build.js`
    - Navigation
    - Guards
    - Route lifecycle

- **Guards**: `guard.js`, `guard.result.js`
    - Authentication protection
    - Feature-level route validation

- **Http**: `http.js`, `HttpError.js`, `NetworkError.js`
    - Fetch abstraction
    - Response normalization
    - Throwing domain-aware errors

- **Session**: `session.js`
    - Token storage
    - Identity management
    - Login / logout state

- **Registry**: `registry.js`
    - Lightweight dependency container used to:
    - Register feature components
    - Resolve instances


### 3. `features/` – Feature Modules
Each feature is self-contained and follows a mini clean-architecture style.

| File              | Responsibility               |
| ----------------- | ---------------------------- |
| `*.route.js`      | Route registration           |
| `*.controller.js` | Orchestration logic          |
| `*.usecase.js`    | Business logic               |
| `*.component.js`  | UI interaction logic         |
| `*.view.js`       | HTML rendering               |
| `*.guard.js`      | Route protection             |
| `error/*`         | Feature-specific error model |


This ensures:
- Business error isolation
- Feature independence
- Explicit error codes
- No leaking backend structure

### 4. `shared/` – Cross-Feature Reusables

- **API Clients**:
    - `shared/api/`
    - Responsible for calling Gateway endpoints.

- **Shared Error System**:
    - `shared/error/`
    - This ensures:
        - All errors become predictable
        - Transport errors are converted
        - Feature errors extend shared base

- **Shared Components**:
    - `shared/components/`
    - Include `navbar.js`, `flash.js`, etc.
    - Resuable UI components.


## How to run

**Simple HTTP server**
```bash
cd frontend/web
python3 -m http.server 3000
```

Open:
```bash
http://localhost:3000
```

---

## How To Add a New Feature
1. Create folder inside features/
2. Add:
    - new API call in `shared/api` (if needed)
    - route
    - controller
    - usecase
    - view
    - component
3. Register route
4. Register error handler (if needed)
5. Register in registry

---

## Design Principles

- No hidden magic
- Explicit flows
- Predictable error system
- Feature isolation
- Gateway-first communication
- No tight coupling with backend internals

---

## More Information
For detailed frontend architecture and rules, see:

### Frontend Architecture

### Governance & Design Rules
- [General Coding Standards](./../../docs/rules/governance.md)

### Internal Error System
For deeper understanding of the error abstraction layer:
[`js/shared/error/README.md`](./js/shared/error/README.md)
