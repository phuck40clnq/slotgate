# test-service

A sandbox service with no business logic, used to check routing, headers, error handling and logs through the gateway. For anyone who changes the gateway or the shared service setup.

## Why it exists

A change to routing or to the shared setup can be tried here without touching a real service or its data. It keeps no state and takes part in no event flow.

## API and events

The gateway routes `/api/test/**` here without asking for a token.

| Endpoint | Answer |
|---|---|
| `GET /api/v1/test/hello` | `{ "userId": ... }` from the required header `X-User-Id` |
| `GET /api/v1/test/error-400` | `400` with the code `INVALID_INPUT` |
| `GET /api/v1/test/error-404` | `404` with the code `USER_NOT_FOUND` |
| `GET /api/v1/test/error-500` | `500` from an unhandled exception |

The error bodies hold `code` and `message` only, not the `ErrorResponse` of API-17.

## Configuration

| Variable | Meaning |
|---|---|
| `SERVICE_NAME` | Name in logs; defaults to `test-service` |
| `PORT` | HTTP port; defaults to `6868` |
| `SPRING_PROFILES_ACTIVE` | Fills the `env` field of the logs; the service has no profile files |

## Run

```bash
cd services/test-service
./mvnw spring-boot:run
```

## Test

```bash
cd services/test-service
./mvnw verify
```

The only test checks that the application starts.
