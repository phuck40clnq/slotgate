# platform/java

The shared build baseline of every Java service: one module for dependency versions and one for build plugins. For anyone who adds a dependency or changes how Java services build and test.

## Why it exists

CODE-35 keeps dependency versions in one place per language, so services cannot drift apart. Services inherit the same compiler, test and coverage setup, so every Java service is checked the same way.

## Run

Install both modules once per machine, and again after any change here, because services read the installed copies:

```bash
mvn -f platform/java/java-bom/pom.xml install
mvn -f platform/java/java-parent/pom.xml install
```

## Details

| Module | Holds |
|---|---|
| `java-bom` | The versions of Spring Boot, Spring Cloud, Spring Kafka, Avro, the Kafka Avro serializer, the PostgreSQL driver, Micrometer Tracing, ArchUnit, embedded PostgreSQL and the Logstash encoder |
| `java-parent` | Java 21 and the inherited plugins: compiler, enforcer, Surefire for `*Test`, Failsafe for `*IT`, JaCoCo for TEST-28, Avro code generation, Spring Boot packaging |

`java-parent` inherits `java-bom`, and each Java service inherits `java-parent`. A new dependency is added to `java-bom` with the reason CODE-36 asks for.
