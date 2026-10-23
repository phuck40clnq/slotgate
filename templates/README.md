# Templates

This directory contains the starting point for every new service, one template per language.

Templates provide:
- Consistent project structure
- Shared configuration conventions
- Alignment with platform and contract policies

---

## Available templates

| Template | Language | Platform baseline |
|---|---|---|
| [spring-boot-service](./spring-boot-service/README.md) | Java 21, Spring Boot 3 | [platform/java](../platform/README.md) |

---

## Creating a service

A new service is always copied from a template, never from another service. The steps are in [services/README.md](../services/README.md#adding-a-new-service).

---

## Adding a language

A service may use another language when the job justifies it. Before the first service in a new language:

1. Record the decision in an [ADR](../docs/adr/overview.md).
2. Add the language baseline under `platform/<lang>/`: dependency versions and build configuration.
3. Add `templates/<lang>-service/` with the same operational surface as the existing template: JSON logs with the shared field names, configuration from environment variables, health endpoint and README.

Templates evolve with the platform standards and are updated in the same change as the standard they follow.
