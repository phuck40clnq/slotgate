# auth-service

The service that registers users, checks their passwords and issues the signed tokens the gateway verifies. For anyone who changes how users sign up or sign in.

## Why it exists

Credentials stay in one service, so no other service ever sees a password. It owns credentials only; profiles belong to `user-service`, and permissions to each service.

## API and events

- The endpoints and the token are described in [authentication](../../docs/architecture/authentication.md). They have no OpenAPI contract in `contracts/openapi/`.
- Events, defined in `contracts/avro/auth/events/`:

| Event | Topic | Published when |
|---|---|---|
| `UserRegistered` | `auth-user-events` | A user registers |
| `UserLoggedIn` | `auth-security-events` | A user signs in |

The service also listens to `auth-auth-events`, which nothing in the repository publishes.

## Configuration

| Variable | Meaning |
|---|---|
| `SERVICE_NAME` | Name in logs and Kafka consumer group; defaults to `auth-service` |
| `PORT` | HTTP port; defaults to `8082` |
| `SPRING_PROFILES_ACTIVE` | `local` for the local database and Kafka at `localhost:9092` |
| `DB_NAME`, `DB_USER`, `DB_PASS` | Database `slotgate_auth`; the `local` defaults match `infrastructure/postgres/` |
| `OAUTH_JWK_SET_URI` | Written into the `iss` claim of every token; required, without default |
| `SCHEMA_REGISTRY_URL` | Schema Registry; defaults to `http://localhost:8081` |

## Run

Generate the key pair once, as step 3 of the root [`README.md`](../../README.md#getting-started) shows, then:

```bash
cd services/auth-service
SPRING_PROFILES_ACTIVE=local OAUTH_JWK_SET_URI=http://localhost:8082/oauth2/jwks ./mvnw spring-boot:run
```

Registering and signing in publish events, so they need Kafka and Schema Registry from [`infrastructure/kafka/`](../../infrastructure/kafka/README.md).

## Test

```bash
cd services/auth-service
./mvnw verify
```

The only test checks that the application starts.

## Details

- Why tokens are JWTs signed with RSA: [ADR-0003](../../docs/adr/0003-use-jwt-rsa.md)
- Signing: `infrastructure/security/jwt/`
- Events: `infrastructure/messaging/kafka/producer/`
