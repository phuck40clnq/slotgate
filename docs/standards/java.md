# Java Standard

## Table of contents
- [Scope](#scope)
- [Package layout](#package-layout)
- [Language](#language)
- [Null handling](#null-handling)
- [Lombok](#lombok)
- [Time, identifiers and randomness](#time-identifiers-and-randomness)
- [Spring](#spring)
- [Errors](#errors)
- [Messaging](#messaging)
- [Persistence](#persistence)
- [Logging](#logging)
- [Documentation comments](#documentation-comments)
- [Formatting](#formatting)

---

## Scope

Rules for Java 21 and Spring Boot 3 code in `services/` and `templates/`. This file extends [coding.md](./coding.md) and does not repeat it. Test code follows [testing.md](./testing.md).

---

## Package layout

Every service MUST use this layout under `com.example.<service>`:

| Package | Contains | May depend on |
|---|---|---|
| `domain` | Models, value objects, domain events, domain rules | JDK, Lombok |
| `application` | `port.in` (`*UseCase`), `port.out` (`*Port`), `usecase` (`*Service`), `dto` (`*Command`, `*Result`) | `domain`, `common`, Spring stereotypes, `@Transactional` |
| `adapter` | Inbound adapters: `web` (controllers, `*Request`, `*Response`), `messaging` (event handlers), `system` | `application`, `common` |
| `infrastructure` | Outbound adapters: `persistence`, `messaging`, `security` | `application`, `domain`, `common`, any library |
| `config` | Spring wiring and `@ConfigurationProperties` | anything |
| `common` | `error` (codes, `ErrorResponse`, handler), `exception`, `filter` | JDK, Spring web |

- `adapter` MUST NOT call `infrastructure`; it goes through an inbound port.
- `domain` MUST NOT import Spring, Jakarta, Jackson, Kafka, Avro or JPA types.
- Persistence entities (`Jpa*Entity`) and Avro records MUST NOT leave `infrastructure`.

---

## Language

- Records MUST be used for DTOs, commands, results, value objects and configuration properties.
- Closed sets of outcomes MUST be sealed interfaces handled with pattern-matching `switch`, not flags, nulls or status strings.
- `switch` over an enum or sealed type MUST be exhaustive without a `default` branch, so a new case fails compilation.
- `var` MAY be used only when the type is obvious from the right-hand side.
- Collections MUST NOT be `null`. Return empty or unmodifiable collections (`List.copyOf`, `Map.copyOf`).
- Raw types, wildcard imports and unused imports MUST NOT appear.
- Checked exceptions MUST NOT cross a port.
- Streams SHOULD be used only when clearer than a loop. A stream pipeline MUST NOT have side effects.

---

## Null handling

- Parameters, fields and return values are non-null by default.
- A value that can be absent MUST be returned as `Optional`. `Optional` MUST NOT be a field, parameter or collection element.
- Constructors MUST validate arguments with `Objects.requireNonNull(value, "name")` and explicit range checks.

---

## Lombok

- Allowed: `@RequiredArgsConstructor`, `@Getter`, `@Slf4j`.
- Forbidden: `@Data`, `@Setter`, `@Value`, `@AllArgsConstructor`, `@NoArgsConstructor` outside JPA entities, `@SneakyThrows`, Lombok `@NonNull`, and `@Builder` on domain types.
- Use records, explicit constructors and named static factories instead, for example `restore(...)` to rebuild a domain object from storage.

---

## Time, identifiers and randomness

- Domain and application code MUST NOT call `Instant.now()`, `LocalDateTime.now()`, `UUID.randomUUID()` or `new Random()`.
- Time comes from an injected `java.time.Clock`; identifiers come from an injected generator port. Tests control both.
- Time MUST be stored and transported as `Instant` in UTC. Time zones apply only at presentation.

---

## Spring

- Injection MUST be through constructors (`@RequiredArgsConstructor`). Field and setter injection MUST NOT be used.
- Controllers MUST be thin: map `*Request` to `*Command`, call one use case, map `*Result` to `*Response`. No business logic, no repositories.
- Request DTOs MUST be validated with Bean Validation (`@Valid`, constraint annotations) at the controller.
- `@Transactional` MUST be placed on use case methods only. A transaction MUST NOT span a remote call or a broker publish.
- Configuration properties MUST be records annotated with `@ConfigurationProperties` and `@Validated`.
- `@Bean` methods MUST live in `config`.
- Profiles are `local`, `dev` and `prod`. Defaults in `application.yml` MUST be safe for any profile; secrets come only from the environment.

---

## Errors

- Business rule violations MUST throw `BusinessException` with a code from the service's `*BusinessErrorCode` enum.
- Technical failures MUST throw `InfrastructureException` with a code from `*InfrasErrorCode`, keeping the original cause.
- Use cases MUST NOT throw `RuntimeException`, `IllegalStateException` or `IllegalArgumentException` directly.
- Only the service's global exception handler turns exceptions into HTTP responses, following the `ErrorResponse` contract in [contracts/openapi/error](../../contracts/openapi/error/README.md).
- `catch (Exception e)` MUST appear only in that handler and in message listeners, and MUST log or rethrow.

---

## Messaging

- Producing MUST follow: domain event → `*Mapper` (domain to Avro) → `*Router` (topic and key) → producer port.
- Consuming MUST follow: listener → `*Translator` (Avro to domain event) → `On<Event>` handler in `adapter.messaging.handlers` → use case.
- The record key MUST be the aggregate identifier, so events of one aggregate stay ordered.
- Handlers MUST be idempotent: deduplicate by `eventId` before changing state.
- Failed records MUST go to a dead-letter topic after bounded retries with backoff. They MUST NOT be dropped or retried forever.
- Writing state and publishing an event MUST be atomic through the transactional outbox once it exists. Until then, code that does both in separate steps is a known risk and MUST be listed in an issue.

---

## Persistence

- Schema changes MUST be Flyway migrations in `src/main/resources/db/migration/V<n>__<description>.sql`. Hibernate `ddl-auto` MUST NOT be relied on outside the `local` profile.
- Invariants that protect correctness (for example reserved seats never exceed capacity) MUST also be database constraints, not only Java checks.
- Repositories MUST be reached only through `*Port` interfaces and return domain objects.
- Operations that must be atomic under concurrency MUST state their locking or atomic primitive explicitly and MUST have a concurrency test.

---

## Logging

- Loggers come from `@Slf4j`. Messages use SLF4J placeholders: `log.info("Seat reserved: seatId={}, studentId={}", seatId, studentId)`.
- An exception MUST be the last argument, so the stack trace reaches the `errorStack` field.
- An argument without a matching `{}` placeholder is silently dropped; every value MUST have one.
- Values to search across services go in the MDC. `logback-spring.xml` exports `traceId` and `correlationId`; a new key MUST be added to it in every service.

---

## Documentation comments

- Public ports and use cases MUST have a doc comment: a one-sentence summary and one `@throws` line per business error code.
- Doc comments MUST NOT restate parameter names or types.
- Getters, records, controllers and private methods MUST NOT have doc comments unless they carry a non-obvious contract.

---

## Formatting

- Indentation is 4 spaces; line length is 120 characters.
- Import order: static imports, `java`, `javax`/`jakarta`, other libraries, `com.example`. One blank line between groups.
- Member order: constants, fields, constructors, public methods, private methods.
- One top-level type per file.
