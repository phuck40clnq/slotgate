# Platform

This directory contains shared platform-level configuration and standards used across services.

It defines the technical baseline for building and running services in this architecture.

---

## Purpose

- Standardize dependency management
- Standardize build configuration
- Reduce duplication across services
- Provide a consistent development baseline

---

## Structure

Currently:

```code
platform
├── README.md
└── java
    ├── java-bom    # Centralized dependency versions
    └── java-parent # Shared build configuration; inherits java-bom, services inherit java-parent
```

Additional languages or tooling (e.g., Go, Python, CI, Docker) may be added over time.
