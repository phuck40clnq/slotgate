# user-service

The service that keeps user profiles, created from the registrations of `auth-service`. For anyone who changes what a profile holds or how it is read and edited.

## Why it exists

A profile changes for other reasons than a password, so profiles live apart from credentials. The service builds them from events, without calling `auth-service`, as CORE-08 requires.

## API and events

The caller is the user in `X-Internal-User-Id`. The contract `contracts/openapi/user/user.openapi.yaml` does not describe these endpoints yet.

| Endpoint | What it does |
|---|---|
| `GET /api/v1/user/me/profile` | Returns `userId`, `email`, `fullName` and `status` of the caller |
| `PUT /api/v1/user/me/profile/edit` | Changes `fullName` and `avatarUrl` of the caller |

| Event | Topic | What the service does |
|---|---|---|
| `UserRegistered` | `auth-user-events` | Creates a profile with the full name `Default full name` |

The consumer starts from the latest offset and does not skip an event it has already applied.

## Configuration

| Variable | Meaning |
|---|---|
| `SERVICE_NAME` | Name in logs and Kafka consumer group; defaults to `user-service` |
| `PORT` | HTTP port; defaults to `8083` |
| `SPRING_PROFILES_ACTIVE` | `local` for the local database and Kafka at `localhost:9092` |
| `DB_NAME`, `DB_USER`, `DB_PASS` | Database `slotgate_user`; the `local` defaults match `infrastructure/postgres/` |
| `SCHEMA_REGISTRY_URL` | Schema Registry; defaults to `http://localhost:8081` |

## Run

```bash
cd services/user-service
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

Profiles are created only from events, so registration needs Kafka and Schema Registry from [`infrastructure/kafka/`](../../infrastructure/kafka/README.md).

## Test

```bash
cd services/user-service
./mvnw verify
```

The only test checks that the application starts.

## Details

- Event handling: `adapter/messaging/handlers/OnUserRegistered.java`
- Event translation: `infrastructure/messaging/kafka/consumer/`
