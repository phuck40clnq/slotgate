# gateway-service

The only public entry of Slot Gate: it routes client requests to the services, verifies their token and passes on the caller's identity. For anyone who adds a route or changes how requests enter the platform.

## Why it exists

CORE-12 makes the gateway the only public entry, so token checks, identity, cross-origin rules and the error format are enforced in one place, and the services stay private. It holds no business logic and no state, and forwards each request to one service, so more instances can be added under load.

## API and events

| Public path | Forwarded to | Token |
|---|---|---|
| `/api/auth/**` | `auth-service` at `/api/v1/auth/**` | Not required |
| `/api/user/**` | `user-service` at `/api/v1/user/**` | Required |
| `/api/seat-inventory/**` | `seat-inventory-service` at `/api/v1/seat-inventory/**` | Required |
| `/api/test/**` | `test-service` at `/api/v1/test/**` | Not required |

- Token checks and the `X-Internal-User-Id` header work as [authentication](../../docs/architecture/authentication.md) describes.
- Every error, including token, routing and network errors, is answered as an `ErrorResponse`, with `traceId` taken from the `X-Trace-Id` request header, or `N/A` without it.
- Cross-origin requests are accepted from `http://localhost:3000`, with the methods `GET`, `POST`, `PUT`, `DELETE` and `OPTIONS`, the headers `Authorization` and `Content-Type`, and credentials.
- The gateway publishes and consumes no event.

## Configuration

| Variable | Meaning |
|---|---|
| `SERVICE_NAME` | Name in logs; defaults to `gateway-service` |
| `PORT` | HTTP port; defaults to `8080` |
| `SPRING_PROFILES_ACTIVE` | `local` sends each route to the fixed local port of its service |
| `OAUTH_JWK_SET_URI` | Key set of `auth-service`, such as `http://localhost:8082/oauth2/jwks` |

## Run

`auth-service` must run first, because the gateway reads its keys:

```bash
cd services/gateway-service
SPRING_PROFILES_ACTIVE=local OAUTH_JWK_SET_URI=http://localhost:8082/oauth2/jwks ./mvnw spring-boot:run
```

## Test

```bash
cd services/gateway-service
./mvnw verify
```

The service has no automated tests.

## Details

- Routes: `src/main/resources/application.yml`, and the local ports in `application-local.yml`
- Public paths: `config/SecurityConfig.java`
- Identity header: `filter/identity/IdentityPropagationFilter.java`
- Why tokens are JWTs signed with RSA: [ADR-0003](../../docs/adr/0003-use-jwt-rsa.md)
