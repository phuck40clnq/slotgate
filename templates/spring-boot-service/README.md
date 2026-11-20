# templates/spring-boot-service

The template that every Java service is copied from, built on [`platform/java`](../../platform/java/README.md). For anyone who creates a Java service or changes what all of them share.

## Why it exists

SVC-01 starts every service from a template, never from another service, so a new service meets the [service standard](../../docs/standards/services.md) from its first commit and inherits no debt.

## Configuration

`.env.example` lists `SERVICE_NAME`, `PORT` and `SPRING_PROFILES_ACTIVE`; a service adds its own variables below them.

## Test

```bash
cd templates/spring-boot-service
./mvnw verify
```

`ArchitectureTest` checks the package layout of the [Java standard](../../docs/standards/java.md), and `SampleApplicationTests` checks that the application starts.

## Details

The template holds:

- The parent POM from `platform/java`, so versions and build plugins are shared
- Spring Web, Actuator, Spring Kafka, Spring Data JPA, Lombok and the Logstash encoder
- `logback-spring.xml` with the log fields of SVC-06
- `health` and `metrics` as the only exposed management endpoints
- The Maven wrapper, so a service builds without a local Maven

When copying it, as the [guide](../../docs/guides/add-a-service.md) shows, replace every sample value, then remove the dependencies the service does not use:

| Item | Template value | Replace with |
|---|---|---|
| Directory | `templates/spring-boot-service` | `services/<name>-service` |
| `artifactId` and `name` in `pom.xml` | `sample-service` | `<name>-service` |
| Base package | `com.example.sample` | `com.example.<name>` |
| Application class | `SampleServiceApplication` | `<Name>ServiceApplication` |
| `SERVICE_NAME` default in `application.yml` | `sample-service` | `<name>-service` |
| `PORT` default in `application.yml` | `8999` | The next free port |
| This README | The template description | The README of the service, following DOC-59 |
