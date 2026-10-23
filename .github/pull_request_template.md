<!--
Title format: <type>(<scope>): <summary>   e.g. feat(user): ignore duplicate UserRegistered events
Rules: https://github.com/phuck40clnq/slotgate/blob/main/CONTRIBUTING.md#pull-requests
Every checklist item must be checked, or left unchecked with a one-line reason next to it.
-->

## Summary

<!-- What changes and why, in one to three sentences. A reader should understand the purpose without opening the diff. -->

## Related issue

Closes #

<!-- Also list related issues, ADRs or PRD sections: Refs #, ADR-000X -->

## Type of change

- [ ] `feat` New behaviour
- [ ] `fix` Defect fix
- [ ] `refactor` No behaviour change
- [ ] `perf` Performance improvement (measurements below)
- [ ] `test` Tests only
- [ ] `docs` Documentation only
- [ ] `build` / `ci` / `chore`
- [ ] **Breaking change** to a contract, API, event, configuration or data format

## Changes

<!-- The main changes, grouped by service or area. Mention anything a reviewer could miss. -->

-

## How it was verified

<!-- Exact commands and their results. Paste the relevant output, not just "tests pass". -->

```text
$ ./mvnw verify
```

<!-- For behaviour visible through the API or UI: requests, responses, logs or screenshots. -->

## Impact

| Area | Impact |
|---|---|
| Contracts (OpenAPI / Avro) | None |
| Database schema / migrations | None |
| Configuration / environment variables | None |
| Performance (with before/after numbers) | None |
| Security / privacy | None |
| Observability (logs, metrics, traces) | None |
| Deployment / operations | None |

## Risks and rollback

<!-- What could go wrong in production and how to undo it. Write "None" only when it is true. -->

---

## Definition of Done

### Scope and traceability
- [ ] The linked issue's acceptance criteria are all met, and nothing outside its scope is changed
- [ ] The title follows Conventional Commits, and every commit builds and passes its tests
- [ ] The pull request changes at most 400 lines (excluding generated files), or is a stated mechanical change

### Code quality
- [ ] Code follows the [coding](https://github.com/phuck40clnq/slotgate/blob/main/docs/rules/coding.md) and [Java](https://github.com/phuck40clnq/slotgate/blob/main/docs/rules/java.md) standards
- [ ] No `TODO`/`FIXME`, dead code, commented-out code, empty stubs or placeholder files
- [ ] No suppressed checks, or each suppression names the check and its reason on the same line
- [ ] Everything this change replaces is removed (classes, configuration keys, dependencies, files, links)
- [ ] Layering holds: domain imports no framework, adapters go through ports

### Tests
- [ ] New or changed behaviour has tests that fail without this change ([testing standard](https://github.com/phuck40clnq/slotgate/blob/main/docs/rules/testing.md))
- [ ] A bug fix includes a test that reproduces the bug
- [ ] Adapters are tested against real infrastructure, concurrency-sensitive code has a concurrency test
- [ ] `./mvnw verify` passes in every changed service, and coverage stays above the thresholds
- [ ] No disabled, skipped or flaky tests were introduced

### Contracts and compatibility
- [ ] Contracts in `contracts/` were changed before the implementation
- [ ] Changes are backward compatible, or the break is declared with `!` and a `BREAKING CHANGE:` footer
- [ ] Every copy of a changed schema matches `contracts/`, and every consumer is updated

### Data and configuration
- [ ] Schema changes are Flyway migrations that run on an existing database without manual steps
- [ ] New environment variables are listed in `.env.example` with safe local defaults
- [ ] No secrets, credentials or real personal data are committed

### Reliability and observability
- [ ] Failures are handled explicitly: bounded retries, dead-letter handling, no swallowed exceptions
- [ ] Event consumers are idempotent by `eventId`
- [ ] New log lines are structured, carry identifiers, and contain no sensitive data
- [ ] Errors use stable error codes from the service's error code enums

### Security
- [ ] Input is validated at the boundary
- [ ] New endpoints are behind the gateway with the intended authentication
- [ ] New dependencies are justified in this description and come from the platform BOM

### Documentation and decisions
- [ ] READMEs, architecture docs and `.env.example` reflect the change
- [ ] Architectural decisions have an ADR in `docs/adr/`, linked above
- [ ] A failure found while working is recorded in `docs/incidents/` or as an issue

### Review
- [ ] I read the full diff on GitHub as a reviewer would
- [ ] CI is green, with no check skipped or re-run until it passed
