# Gateway fails to start with the empty values of its .env.example

| Field | Value |
|---|---|
| Detected | 2026-11-27 03:18 UTC |
| Status | resolved |
| Severity | minor |
| Services | `gateway-service` |
| Commit | `8654a10` |
| Related | SVC-04, SVC-05 |

## Summary

Started with the values of its `.env.example`, the gateway stopped before serving anything, twice. An empty `PORT` breaks the HTTP client of Reactor Netty, which reads that variable itself. An empty `SERVICE_NAME` replaces the default name with an empty one, which Brave tracing rejects. Only local runs were affected. The same pull request writes `SERVICE_NAME=gateway-service` and `PORT=8080` in `.env.example`, and the gateway starts with its values.

## Timeline

| Time (UTC) | Event |
|---|---|
| 03:18 | The gateway, started with every value of `.env.example`, stops at start-up on `PORT` |
| 03:19 | Started again without `PORT`, it stops on `SERVICE_NAME` |
| 04:49 | With `PORT=8082` and a key set URI without a port, the key set is fetched from port 80 |
| 04:49 | The same run without `PORT` fetches it from port 80 too |
| 05:13 | Started with every value of the fixed `.env.example`, the gateway answers its health check |

## Symptoms

A check of the gateway `.env.example` started the gateway with exactly its values, `SERVICE_NAME` and `PORT` empty as SVC-05 allows. The application context failed to start, and Maven reported `BUILD FAILURE`.

## Evidence

Run with every value of `.env.example`:

```bash
env SERVICE_NAME= PORT= SPRING_PROFILES_ACTIVE= OAUTH_JWK_SET_URI=http://localhost:8082/oauth2/jwks CORS_ALLOWED_ORIGINS=http://localhost:3000 services/gateway-service/mvnw -f services/gateway-service/pom.xml spring-boot:run
```

```text
Caused by: java.lang.ExceptionInInitializerError: null
	at reactor.netty.http.client.HttpClient.create(HttpClient.java:405)
Caused by: java.lang.IllegalArgumentException: Invalid environment variable [PORT=].
	at reactor.netty.http.client.HttpClientConnect.<clinit>(HttpClientConnect.java:736)
Caused by: java.lang.NumberFormatException: For input string: ""
```

Run without `PORT`, the other values unchanged:

```text
java.lang.IllegalArgumentException:  is not a valid serviceName
	at brave.Tracing$Builder.localServiceName(Tracing.java:169)
	at org.springframework.boot.actuate.autoconfigure.tracing.BraveAutoConfiguration.braveTracing(BraveAutoConfiguration.java:122)
```

The static initialiser of `HttpClientConnect` in `reactor-netty-http` 1.2.2, read with `javap -c -p`, calls `System.getenv("PORT")`, parses it with `Integer.parseInt`, and stores the result, or `80` without the variable, in `DEFAULT_PORT`. Within that class, its only reader builds the default remote address `localhost:DEFAULT_PORT`; other classes of the package were not read.

Run with `PORT=8082` and `OAUTH_JWK_SET_URI=http://localhost/oauth2/jwks`, `auth-service` on 8082, one request with a token:

```text
502
"message":"Gateway error: code=BAD_GATEWAY, traceId=N/A, message=Could not obtain the keys"
Connection refused
localhost/127.0.0.1:80
```

The same run without `PORT` answered `502` with the same `localhost/127.0.0.1:80`.

## Investigation

| # | Hypothesis | How it was tested | Result |
|---|---|---|---|
| 1 | An empty `PORT` breaks a library, not the gateway configuration | Start with `PORT=`; read the bytecode of `HttpClientConnect` | Confirmed: Reactor Netty parses `PORT` in a static initialiser |
| 2 | An empty `SERVICE_NAME` replaces the default of `${SERVICE_NAME:gateway-service}` | Start without `PORT`, with `SERVICE_NAME=` | Confirmed: a placeholder default applies only to an absent variable, and Brave rejects the empty name |
| 3 | A non-empty `PORT` becomes the default port of every client request | Key set URI without a port, with `PORT=8082` and without it | Rejected: both runs connected to port 80, the default of the scheme |

## Root cause

SVC-05 lets `.env.example` leave a variable empty, but an empty variable is set, not absent. A placeholder default does not apply to it, and a library may read the variable itself, as Reactor Netty reads `PORT`.

## Resolution

`.env.example` gives both variables a safe local default, which SVC-05 already allows: `SERVICE_NAME=gateway-service` and `PORT=8080`. Renaming the port variable, which would change SVC-04, was rejected: row 3 found no harm in a non-empty `PORT`. Before, the gateway stopped at start-up; after, with every value of `.env.example`:

```text
"message":"Started GatewayServiceApplication in 17.514 seconds (process running for 20.962)"
curl -s -w ' %{http_code}\n' http://localhost:8080/actuator/health
{"status":"UP"} 200
```

## Lessons

- Start a service with the values of its `.env.example` before relying on them.
- A generic name such as `PORT` can be read by a library as well as by the service.
- The template and the other services leave `SERVICE_NAME` and `PORT` empty too, and are fixed in their own change.
