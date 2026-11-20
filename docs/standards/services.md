# Service standard

What every Slot Gate service provides in any language, so that all services are built, run and operated the same way. Rules use the prefix `SVC`, and `docs/standards/<language>.md` says how each language meets them.

## Creation and layout

| ID | Rule | Check |
|---|---|---|
| SVC-01 | A service MUST be created from `templates/<language>-service/` and MUST NOT be copied from another service, because a copy carries that service's history and debt. | Review |
| SVC-02 | A service MUST live in `services/<name>-service/` with its own build wrapper, `Dockerfile`, `.env.example` and `README.md`. | Review |
| SVC-03 | A service MUST take the next free port and add it to the component table of the root `README.md`. | Review |

## Configuration

| ID | Rule | Check |
|---|---|---|
| SVC-04 | A service MUST read `SERVICE_NAME` and `PORT` from the environment, and run in one of the environments `local`, `dev` and `prod`. | Review |
| SVC-05 | Every variable in `.env.example` MUST have an empty value or a safe local default, and a comment when its meaning or format is not plain from its name. | Review |

## Logs

| ID | Rule | Check |
|---|---|---|
| SVC-06 | Each log line MUST be one JSON object on standard output, with exactly the fields of the table below. | Review |
| SVC-07 | A new field MUST be added to this table and to every service in the same pull request. | Review |

| Field | Content |
|---|---|
| `timestamp` | When the line was written, in UTC |
| `level` | `ERROR`, `WARN`, `INFO`, `DEBUG` or `TRACE` |
| `service` | The value of `SERVICE_NAME` |
| `env` | The environment: `local`, `dev` or `prod` |
| `logger` | The component that wrote the line |
| `thread` | The thread or task that wrote the line |
| `message` | The message with its arguments filled in |
| `mdc.traceId` | The request ID of the request being served |
| `mdc.correlationId` | The ID that ties together the requests of one flow |
| `errorStack` | The stack trace, when the line reports an error |

## Request IDs

| ID | Rule | Check |
|---|---|---|
| SVC-08 | A service MUST take the request ID from the `X-Request-Id` header, create a UUID when the header is absent, and return it in `X-Request-Id`. | Review |
| SVC-09 | A service MUST keep the request ID in its log context as `traceId` and `correlationId` until the request ends. | Review |
| SVC-10 | A service MUST send `X-Request-Id` on every HTTP call it makes. | Review |

## Health and metrics

| ID | Rule | Check |
|---|---|---|
| SVC-11 | A service MUST serve its health at `GET /actuator/health` and its metrics at `GET /actuator/metrics`. | `curl` |
| SVC-12 | A service MUST NOT expose any other management endpoint, because such endpoints reveal configuration and internals. | Review |

## Data

| ID | Rule | Check |
|---|---|---|
| SVC-13 | A service with state MUST own one PostgreSQL database and one role, both named `slotgate_<name>`, created in `infrastructure/postgres/create-service-databases.sql`. Another datastore needs an ADR. | Review |
| SVC-14 | A schema change MUST be a versioned migration applied at start-up; generating the schema from code MUST NOT be used outside `local`. | Review |
| SVC-15 | A merged migration MUST NOT change, and a migration that writes data MUST be tested against a database that already holds rows. | Test |

## Container image

| ID | Rule | Check |
|---|---|---|
| SVC-16 | The `Dockerfile` MUST build an image that runs the service as a non-root user and reads its configuration only from the environment. | Review |
