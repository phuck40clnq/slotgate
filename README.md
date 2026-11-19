# Slot Gate

The repository of Slot Gate, a course registration platform shared by many schools, for anyone who builds or runs it. Slot Gate runs on simulated data based on public regulations and holds no real personal data.

## Start here

1. [Product](docs/core/product.md): what Slot Gate is, who uses it and the targets it is measured against
2. [Core rules](docs/core/rules.md): what no change may break
3. [Documentation standard](docs/README.md): which documents exist, when they are written and how

## Components

| Component | Port | What it does |
|---|---|---|
| [`gateway-service`](services/gateway-service/README.md) | 8080 | The only public entry: routing, JWT verification, identity headers, unified errors |
| [`auth-service`](services/auth-service/README.md) | 8082 | Registration, login, RS256 JWT issuance and JWKS; publishes `UserRegistered` and `UserLoggedIn` |
| [`user-service`](services/user-service/README.md) | 8083 | User profiles; consumes `UserRegistered` |
| [`seat-inventory-service`](services/seat-inventory-service/README.md) | 8084 | Sections and seat holds that never exceed capacity, with confirmation and expiry |
| [`test-service`](services/test-service/README.md) | 6868 | Sandbox for routing, contract and event experiments |
| [`frontend/web/`](frontend/web/README.md) | | Web client: login, registration, profile |
| [`contracts/`](contracts/README.md) | | OpenAPI and Avro contracts between services and clients |
| [`platform/java/`](platform/java/README.md) | | Shared build baseline of Java services |
| [`templates/`](templates/README.md) | | Starting point for a new service per language |
| [`infrastructure/postgres/`](infrastructure/postgres/README.md) | 5432 | One PostgreSQL database and role per service |
| [`infrastructure/kafka/`](infrastructure/kafka/README.md) | 9092, 8081, 2181 | Kafka, Schema Registry and ZooKeeper |

How the components work together is in the [architecture overview](docs/architecture/overview.md). A new service takes the next free port and is added to this table.

## Getting started

Requirements: Java 21, Maven 3.9 or later, and PostgreSQL 14 or later on `localhost:5432`. Docker with Compose is needed only to run Kafka.

1. Install the shared build files, which services inherit and no repository publishes:

   ```bash
   mvn -f platform/java/java-bom/pom.xml install
   mvn -f platform/java/java-parent/pom.xml install
   ```

2. Create one database and role per service, once:

   ```bash
   psql -d postgres -f infrastructure/postgres/create-service-databases.sql
   ```

3. Generate the JWT key pair of `auth-service`, once per machine; both files stay out of git:

   ```bash
   mkdir -p services/auth-service/src/main/resources/keys
   cd services/auth-service/src/main/resources/keys
   openssl genpkey -algorithm RSA -out private.pem -pkeyopt rsa_keygen_bits:2048
   openssl rsa -pubout -in private.pem -out public.pem
   cd -
   ```

4. Run a service with the `local` profile:

   ```bash
   cd services/auth-service
   SPRING_PROFILES_ACTIVE=local OAUTH_JWK_SET_URI=http://localhost:8082/oauth2/jwks ./mvnw spring-boot:run
   ```

5. Start Kafka, Schema Registry and ZooKeeper for the services that publish or consume events:

   ```bash
   docker compose -f infrastructure/kafka/docker-compose.yml up -d
   ```

## Documentation

- [Contributing](CONTRIBUTING.md): issues, branches, commits and pull requests
- [Standards](docs/standards/): coding, testing, services, APIs, events and Java
- [Architecture decision records](docs/adr/README.md)
- [Incidents](docs/incidents/README.md)
- [Product requirements](docs/prd-2026-10-17.pdf), in Vietnamese, as first agreed with the supervisor

## Origin

Slot Gate is the course project of Technology Project (Dự án Công nghệ) 2026 at VNU University of Engineering and Technology. Its code and documents started from [LearnEventDrivenArchitecture](https://github.com/phuck40clnq/LearnEventDrivenArchitecture) at commit `efff362`, without that history.
