# test-service

A sandbox service with no business logic, used to check routing, headers, error handling and logs through the gateway. For anyone who changes the gateway or the shared service setup.

## Why it exists

A change to routing or to the shared setup can be tried here without touching a real service or its data. It keeps no state and takes part in no event flow.

## API and events

The contract is [`test.openapi.yaml`](../../contracts/openapi/test/test.openapi.yaml), with every status and error code. The gateway routes `/api/test/**` to `/api/v1/test/**` here without asking for a token, and without a caller ID, as the [authentication design](../../docs/architecture/authentication.md) describes.

| Endpoint | Purpose |
|---|---|
| `GET /api/v1/test/me` | Shows whether a caller ID reached the service in `X-Internal-User-Id`; through the gateway, none does |
| `GET /api/v1/test/errors/bad-request` | Answers a client error, to see how it travels back through the gateway |
| `GET /api/v1/test/errors/not-found` | Answers a missing resource, for the same check |
| `GET /api/v1/test/errors/internal` | Fails with an exception that no rule expects, to see the answer and the log line with its stack trace |

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

The integration tests call the service over HTTP on a random port: the caller header, every simulated error, and the request ID.
