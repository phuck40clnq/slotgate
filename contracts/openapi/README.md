# OpenAPI Contract Policy

HTTP contracts define synchronous communication boundaries between services or external clients.

This document defines the governance rules for OpenAPI contracts.

## Principles

- Contracts must remain backward compatible
- Contracts are the source of truth for HTTP interfaces
- Changes must be reviewed carefully

---

## Naming Rules

- Path segments use kebab-case
- operationId MUST be unique and use camelCase

---

## Path Rules

- Use resource-oriented design
- Avoid verbs in paths

Example:
```code
GET /api/v1/user/me/profile
```

---

## Header Rules

- All required custom headers MUST be defined as shared components under `contracts/openapi/headers/`
- Header names use standard HTTP naming conventions (e.g., X-Internal-User-Id)

---

## Request/Response Rules

- All APIs MUST define explicit success response schemas
- All APIs MUST define a shared error response schema

---

## Error Rules

- Error response MUST follow [ErrorResponse](./error/error.openapi.yaml) schema
- Error codes MUST be stable and machine-readable

---

## Versioning Rules

- Version via URL prefix (e.g., /v1)
- Do NOT version in schema names
