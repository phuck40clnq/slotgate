# Testing Standard

## Table of contents
- [Scope](#scope)
- [Principles](#principles)
- [Test types](#test-types)
- [Rules](#rules)
- [Naming and structure](#naming-and-structure)
- [Determinism](#determinism)
- [Test doubles](#test-doubles)
- [Test data](#test-data)
- [Coverage](#coverage)

---

## Scope

Rules for automated tests in every service. This file extends [coding.md](./coding.md); Java specifics are in [java.md](./java.md).

---

## Principles

- **Tests are the specification.** A reader learns what a component does from its tests.
- **Fail fast.** A defect is caught by the cheapest test that can catch it, on the developer's machine before CI.
- **Deterministic.** The same code gives the same result on every run, on every machine.
- **Self-contained.** A test needs one command and no manual setup. A test that needs infrastructure starts it itself.

---

## Test types

| Type | Covers | Runs in | Naming | Tools |
|---|---|---|---|---|
| Unit | Domain and application logic, no framework context, no I/O | `test` phase | `*Test` | JUnit 5, AssertJ |
| Architecture | Layering and forbidden dependencies from [java.md](./java.md) | `test` phase | `ArchitectureTest` | ArchUnit |
| Integration | One adapter against real infrastructure (database, Kafka, HTTP) | `verify` phase | `*IT` | Testcontainers, Spring test slices |
| Contract | Avro compatibility and OpenAPI conformance | CI | - | Schema Registry, OpenAPI linter |
| End-to-end | The running system through the gateway | CI | - | Docker Compose |
| Failure scenario | One scenario from the PRD, reproduced on purpose | On demand | - | Load and fault tools, decided per ADR |

---

## Rules

- Every behaviour change MUST come with a test that fails without the change.
- A bug fix MUST start with a test that reproduces the bug.
- Every rule in `domain` MUST have unit tests for its accepted and rejected cases.
- Every adapter MUST have an integration test against the real technology, not a mock of it.
- Every operation that must be atomic under concurrency MUST have a concurrency test that runs many parallel callers and asserts the invariant (for example: confirmed seats never exceed capacity).
- A test that only checks that the application context starts MUST NOT be the only test of a service.
- Tests MUST NOT be disabled without a reason and an issue: `@Disabled("Fails on leap seconds, see #42")`.
- A flaky test is a defect. It MUST be fixed or removed, never retried until it passes.

---

## Naming and structure

- Test classes mirror the package of the class under test.
- Test method names are sentences in camelCase that state the behaviour: `rejectsHoldWhenSectionIsFull`, `releasesSeatWhenHoldExpires`.
- One behaviour per test. Several assertions are fine when they describe that one behaviour.
- Arrange, act and assert are separated by one blank line each. No `// given` comments.
- Tests MUST NOT contain logic (`if`, loops, `try`). Use parameterized tests for tables of cases.
- Assertions use AssertJ and check observable outcomes, not internal calls.

---

## Determinism

- Time comes from a fixed `Clock`; identifiers from a deterministic generator.
- `Thread.sleep` MUST NOT be used. Asynchronous outcomes are awaited with a bounded timeout (Awaitility).
- Tests MUST NOT depend on execution order or share mutable state.
- Randomised tests MUST use a fixed seed and print it on failure.
- Unit tests MUST NOT touch the network, the file system or the system clock.

---

## Test doubles

- Mocks are allowed only for outbound ports (`*Port`) in application tests.
- Domain objects, value objects and records MUST NOT be mocked; build real ones.
- Libraries and infrastructure the project does not own MUST NOT be mocked; use Testcontainers instead.
- Prefer simple hand-written fakes over mocking frameworks when a fake is shorter.

---

## Test data

- Test data is built through small fixture factories per module (`SeatFixtures.fullSection()`), not copied literals.
- Data is simulated. Real personal data MUST NOT appear in tests, fixtures or seed files.

---

## Coverage

- Line coverage of `domain` and `application` MUST stay at or above 80%, branch coverage at or above 70%. The build fails below that.
- Coverage is a floor, not a goal. A test that executes code without asserting its behaviour is not accepted.
