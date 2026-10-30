# spring-boot-service

Template for Java services: Java 21, Spring Boot 3, built on [platform/java](../../platform/README.md).

---

## What it includes

- Parent POM and BOM from `platform/java`, so dependency versions and build plugins are shared
- Spring Web, Actuator, Spring Kafka, Spring Data JPA and Lombok
- JSON logging through `logback-spring.xml` with the shared field names
- Configuration from environment variables, listed in `.env.example`
- Maven wrapper (`mvnw`), so the service builds without a local Maven
- Test gates: `ArchitectureTest` for layering, `./mvnw verify` for `*IT` tests and the coverage floor

---

## Using the template

Follow [Adding a New Service](../../services/README.md#adding-a-new-service). When copying, replace every sample value:

| Item | Template value | Replace with |
|---|---|---|
| Directory | `templates/spring-boot-service` | `services/<name>-service` |
| `artifactId` and `name` in `pom.xml` | `sample-service` | `<name>-service` |
| Base package | `com.example.sample` | `com.example.<name>` |
| Application class | `SampleServiceApplication` | `<Name>ServiceApplication` |
| `SERVICE_NAME` default in `application.yml` | `sample-service` | `<name>-service` |
| `PORT` default in `application.yml` | `8999` | the next free port |
| This README | template description | the service's purpose, API, events and configuration |

Then remove the dependencies the service does not use.
