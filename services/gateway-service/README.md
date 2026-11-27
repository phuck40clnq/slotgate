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

- Token checks, the paths that pass without a token or are refused, and the `X-Internal-User-Id` header work as [authentication](../../docs/architecture/authentication.md) describes.
- Sections are opened on `seat-inventory-service` itself, not through the gateway, as [its README](../seat-inventory-service/README.md) shows.
- Every error, including token, routing and network errors, is answered as an `ErrorResponse`, with `traceId` taken from the `X-Trace-Id` request header, or `N/A` without it.
- Cross-origin requests are accepted from the origins in `CORS_ALLOWED_ORIGINS`, with the methods `GET`, `POST`, `PUT`, `DELETE` and `OPTIONS`, the headers `Authorization` and `Content-Type`, and credentials.
- The gateway publishes and consumes no event.

## Configuration

| Variable | Meaning |
|---|---|
| `SERVICE_NAME` | Name in logs; defaults to `gateway-service` |
| `PORT` | HTTP port; defaults to `8080` |
| `SPRING_PROFILES_ACTIVE` | `local` sends each route to the fixed local port of its service |
| `OAUTH_JWK_SET_URI` | Key set of `auth-service`, such as `http://localhost:8082/oauth2/jwks` |
| `CORS_ALLOWED_ORIGINS` | Comma-separated origins, each `scheme://host[:port]` without a path or `*`; defaults to `http://localhost:3000`. A value of another form stops the gateway at start-up |

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

The integration tests start the gateway on a random port, with stand-ins for `auth-service` and the services it routes to, so no other service needs to run:

- `SecurityConfigIT`: `GET /actuator/health` answers without a token, and `POST /api/seat-inventory/sections` gets `403` with a token and `401` without one, while `GET` on the same path still reaches the service
- `IdentityPropagationFilterIT`: a service receives the token subject in `X-Internal-User-Id`, never a value the client sent
- `CorsConfigIT`: a preflight passes from an origin in `CORS_ALLOWED_ORIGINS` and gets `403` from any other

`CorsPropertiesTest`, a unit test, checks which values of `CORS_ALLOWED_ORIGINS` the gateway accepts.

## Details

- Routes: `src/main/resources/application.yml`, and the local ports in `application-local.yml`
- Public and refused paths: `config/SecurityConfig.java`
- Identity header: `filter/identity/IdentityPropagationFilter.java`
- Why tokens are JWTs signed with RSA: [ADR-0003](../../docs/adr/0003-use-jwt-rsa.md)
