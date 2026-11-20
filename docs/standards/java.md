# Java standard

How Java 21 and Spring Boot 3 code is written in `services/` and `templates/`, for everyone who writes or reviews it. Rules use the prefix `JAVA`, and they add to the [coding](coding.md), [testing](testing.md), [service](services.md), [API](api.md) and [event](events.md) standards without repeating them.

## Package layout

Every service uses this layout under `com.example.<service>`:

| Package | Contains | May depend on |
|---|---|---|
| `domain` | Models, value objects, domain events, domain rules | The JDK and Lombok |
| `application` | `port.in` (`*UseCase`), `port.out` (`*Port`), `usecase` (`*Service`), `dto` (`*Command`, `*Result`) | `domain`, `common`, Spring stereotypes, `@Transactional` |
| `adapter` | Inbound adapters: `web` (controllers, `*Request`, `*Response`), `messaging` (event handlers), `system` | `application`, `common` |
| `infrastructure` | Outbound adapters: `persistence`, `messaging`, `security` | `application`, `domain`, `common`, any library |
| `config` | Spring wiring and `@ConfigurationProperties` | Anything |
| `common` | `error` (codes, `ErrorResponse`, handler), `exception`, `filter` | The JDK and Spring Web |

| ID | Rule | Check |
|---|---|---|
| JAVA-01 | Code MUST follow the layout above, with each package depending only on what its row allows. | `ArchitectureTest` |
| JAVA-02 | `adapter` MUST NOT call `infrastructure`; it goes through an inbound port. | `ArchitectureTest` |
| JAVA-03 | `domain` MUST NOT import Spring, Jakarta, Jackson, Kafka, Avro or JPA types. | `ArchitectureTest` |
| JAVA-04 | Persistence entities (`Jpa*Entity`) and Avro records MUST NOT leave `infrastructure`. | `ArchitectureTest` |

## Language

| ID | Rule | Check |
|---|---|---|
| JAVA-05 | Records MUST be used for DTOs, commands, results, value objects and configuration properties. | Review |
| JAVA-06 | A closed set of outcomes MUST be a sealed interface handled with a pattern-matching `switch`, not flags, nulls or status strings. | Review |
| JAVA-07 | A `switch` over an enum or a sealed type MUST be exhaustive without a `default` branch, so a new case fails compilation. | Compiler |
| JAVA-08 | `var` MAY be used only when the type is plain from the right-hand side. | Review |
| JAVA-09 | A collection MUST NOT be `null`; methods return empty or unmodifiable collections, as with `List.copyOf`. | Review |
| JAVA-10 | Raw types, wildcard imports and unused imports MUST NOT appear. | Review |
| JAVA-11 | A checked exception MUST NOT cross a port. | Review |
| JAVA-12 | A stream MAY replace a loop only when it reads more plainly, and a stream pipeline MUST NOT have side effects. | Review |
| JAVA-13 | A time quantity MUST be a `Duration` or an `Instant` rather than a number with a unit in its name. | Review |

## Null handling

| ID | Rule | Check |
|---|---|---|
| JAVA-14 | Parameters, fields and return values MUST be non-null unless their type says otherwise. | Review |
| JAVA-15 | A value that can be absent MUST be returned as `Optional`, and `Optional` MUST NOT be a field, a parameter or a collection element. | Review |
| JAVA-16 | A constructor MUST check its arguments with `Objects.requireNonNull(value, "name")` and explicit range checks. | Review |

## Lombok

| ID | Rule | Check |
|---|---|---|
| JAVA-17 | Only `@RequiredArgsConstructor`, `@Getter` and `@Slf4j` MAY be used. | Review |
| JAVA-18 | `@Data`, `@Setter`, `@Value`, `@AllArgsConstructor`, `@SneakyThrows`, Lombok `@NonNull`, `@Builder` on domain types, and `@NoArgsConstructor` outside JPA entities MUST NOT be used. | Review |
| JAVA-19 | Records, explicit constructors and named static factories, such as `restore(...)` to rebuild a domain object from storage, MUST replace the forbidden annotations. | Review |

## Time, identifiers and randomness

| ID | Rule | Check |
|---|---|---|
| JAVA-20 | Domain and application code MUST NOT call `Instant.now()`, `LocalDateTime.now()`, `UUID.randomUUID()` or `new Random()`. | Review |
| JAVA-21 | Time MUST come from an injected `java.time.Clock`, and identifiers from an injected generator port, so that tests control both. | Review |
| JAVA-22 | Time MUST be stored and transported as an `Instant` in UTC; a time zone applies only for display. | Review |

## Spring

| ID | Rule | Check |
|---|---|---|
| JAVA-23 | Injection MUST go through constructors with `@RequiredArgsConstructor`; field and setter injection MUST NOT be used. | Review |
| JAVA-24 | A controller MUST only map a `*Request` to a `*Command`, call one use case, and map its `*Result` to a `*Response`. | Review |
| JAVA-25 | A request DTO MUST be validated with Bean Validation (`@Valid` and constraint annotations) at the controller. | Review |
| JAVA-26 | `@Transactional` MUST be placed on use case methods only, and a transaction MUST NOT span a remote call or a broker publish. | Review |
| JAVA-27 | Configuration properties MUST be records annotated with `@ConfigurationProperties` and `@Validated`. | Review |
| JAVA-28 | `@Bean` methods MUST live in `config`. | Review |
| JAVA-29 | The profiles MUST be `local`, `dev` and `prod`; defaults in `application.yml` MUST be safe for any profile, and secrets MUST come only from the environment. | Review |
| JAVA-30 | `management.endpoints.web.exposure.include` MUST be `health,metrics`, as SVC-11 and SVC-12 require. | Review |

## Errors

| ID | Rule | Check |
|---|---|---|
| JAVA-31 | A business rule violation MUST throw `BusinessException` with a code from the service's `*BusinessErrorCode` enum. | Review |
| JAVA-32 | A technical failure MUST throw `InfrastructureException` with a code from the service's `*InfrasErrorCode` enum, keeping the original cause. | Review |
| JAVA-33 | A use case MUST NOT throw `RuntimeException`, `IllegalStateException` or `IllegalArgumentException` directly. | Review |
| JAVA-34 | Only the global exception handler of the service MUST turn exceptions into HTTP responses, as [`ErrorResponse`](../../contracts/openapi/error/error.openapi.yaml) defines. | Review |
| JAVA-35 | `catch (Exception e)` MAY appear only in that handler and in message listeners, and MUST log or rethrow. | Review |

## Messaging

| ID | Rule | Check |
|---|---|---|
| JAVA-36 | Producing MUST follow: domain event, `*Mapper` from domain to Avro, `*Router` for topic and key, producer port. | Review |
| JAVA-37 | Consuming MUST follow: listener, `*Translator` from Avro to domain event, `On<Event>` handler in `adapter.messaging.handlers`, use case. | Review |
| JAVA-38 | A Kafka serializer MUST use `RecordNameStrategy`, as EVT-12 requires. | Review |

## Persistence

| ID | Rule | Check |
|---|---|---|
| JAVA-39 | Migrations MUST be Flyway files named `src/main/resources/db/migration/V<n>__<description>.sql`. | Review |
| JAVA-40 | `spring.jpa.hibernate.ddl-auto` MUST be `validate` or `none` outside the `local` profile, as SVC-14 requires. | Review |
| JAVA-41 | An invariant that protects correctness, such as reserved seats never exceeding capacity, MUST also be a database constraint. | Review |
| JAVA-42 | A repository MUST be reached only through a `*Port` interface and MUST return domain objects. | `ArchitectureTest` |
| JAVA-43 | An operation that must stay atomic under concurrency MUST name its locking or atomic primitive in the code. | Review |

## Logging

| ID | Rule | Check |
|---|---|---|
| JAVA-44 | Loggers MUST come from `@Slf4j`, and `System.out` and `System.err` MUST NOT be used. | Review |
| JAVA-45 | Messages MUST use SLF4J placeholders, as in `log.info("Seat held: sectionId={}, studentId={}", sectionId, studentId)`. | Review |
| JAVA-46 | An exception MUST be the last argument, so its stack trace reaches `errorStack`. | Review |
| JAVA-47 | A value to search across services MUST go in the MDC, and a new MDC key MUST be added to `logback-spring.xml` in every service, as SVC-07 requires. | Review |

## Tests

| ID | Rule | Check |
|---|---|---|
| JAVA-48 | Tests MUST use JUnit 5 and AssertJ, with `@ParameterizedTest` for tables of cases. | Review |
| JAVA-49 | A unit test MUST be named `*Test` and run in the `test` phase; an integration test MUST be named `*IT` and run in the `verify` phase. | `./mvnw verify` |
| JAVA-50 | A scenario test MUST be named `*Scenario`, so it runs only on demand, as in `./mvnw verify -Dit.test=OversellScenario`. | `./mvnw verify` |
| JAVA-51 | Every service MUST have an `ArchitectureTest` written with ArchUnit that enforces the package layout. | `./mvnw verify` |
| JAVA-52 | An integration test MUST run on embedded PostgreSQL, following [ADR-0004](../adr/0004-run-integration-tests-on-embedded-postgresql.md). | Review |
| JAVA-53 | A disabled test MUST carry its reason and issue, as in `@Disabled("Fails on leap seconds, see #42")`. | Review |
| JAVA-54 | JaCoCo in `platform/java/java-parent` MUST enforce the coverage floor of TEST-28 on `domain` and `application`. | `./mvnw verify` |

## Documentation comments

| ID | Rule | Check |
|---|---|---|
| JAVA-55 | A public port and a use case MUST have a Javadoc with a one-sentence summary and one `@throws` line per business error code. | Review |
| JAVA-56 | A Javadoc MUST NOT restate parameter names or types. | Review |
| JAVA-57 | Getters, records, controllers and private methods MUST NOT have Javadoc unless they carry a contract the code does not show. | Review |

## Formatting

| ID | Rule | Check |
|---|---|---|
| JAVA-58 | Indentation MUST be 4 spaces, and a line MUST NOT exceed 120 characters. | Review |
| JAVA-59 | Imports MUST be grouped as static, `java`, `javax` or `jakarta`, other libraries, then `com.example`, with one blank line between groups. | Review |
| JAVA-60 | Members MUST be ordered as constants, fields, constructors, public methods, then private methods. | Review |
| JAVA-61 | A file MUST hold one top-level type. | Review |
| JAVA-62 | `@SuppressWarnings` MUST name the warning and give its reason in a comment on the same line, as CODE-40 requires. | Review |
