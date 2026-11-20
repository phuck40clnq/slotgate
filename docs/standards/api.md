# API standard

How the HTTP APIs of Slot Gate are designed and described, for anyone who writes a contract or implements one. Rules use the prefix `API`, and the contracts live in `contracts/openapi/`.

## Contracts

| ID | Rule | Check |
|---|---|---|
| API-01 | Every HTTP API MUST be described in `contracts/openapi/<service>/<service>.openapi.yaml`, in OpenAPI 3. | Review |
| API-02 | Every operation MUST have a unique `operationId` in camelCase and a summary in the imperative. | Review |
| API-03 | An operation that changes state MUST describe what happens when it is repeated, retried or called concurrently. | Review |
| API-04 | Every field and parameter MUST have a type, a format where one applies, its limits, a description and an example. | Review |
| API-05 | Every operation MUST list every status it can return, and each error status MUST name its error codes. | Review |
| API-06 | Every operation MUST declare through a security scheme which caller may use it: a student, a school administrator, a school system or the platform. | Review |

## Paths

| ID | Rule | Check |
|---|---|---|
| API-07 | A path MUST be `/api/v1/<service>/<resources>`, with kebab-case segments and plural nouns for collections. | Review |
| API-08 | A path MUST name resources, not actions; a state change is a sub-resource, as in `/holds/{holdId}/confirmation`. | Review |
| API-09 | The calling student's own resources MUST live under `/me/`, as in `/api/v1/seat-inventory/me/holds`. | Review |
| API-10 | The gateway MUST expose `/api/v1/<service>/**` as `/api/<service>/**`, and every other route MUST stay unreachable from outside. | Review |

## Data

| ID | Rule | Check |
|---|---|---|
| API-11 | Field names MUST be camelCase, and identifiers MUST be UUID strings. | Review |
| API-12 | A time MUST be an ISO-8601 string in UTC with the `date-time` format, and a duration an ISO-8601 duration. | Review |
| API-13 | A list MUST be wrapped in an object, as in `{ "holds": [] }`, so that paging can be added without a break. | Review |
| API-14 | A closed set of values MUST be an enum in UPPER_SNAKE_CASE, as in `HELD`. | Review |

## Headers

| ID | Rule | Check |
|---|---|---|
| API-15 | A custom header MUST be defined once, as a shared component under `contracts/openapi/headers/`, and referenced from there. | Review |
| API-16 | The gateway MUST set `X-Internal-User-Id` to the subject of the verified token on every request it forwards, replacing any value a client sent. | Test |

## Errors

| ID | Rule | Check |
|---|---|---|
| API-17 | Every error response MUST be the `ErrorResponse` of [`error.openapi.yaml`](../../contracts/openapi/error/error.openapi.yaml), with `code`, `message` and `traceId`. | Review |
| API-18 | An error code MUST be UPPER_SNAKE_CASE from the service's error code list, and MUST NOT change meaning once published. | Review |
| API-19 | The status MUST match the error, as in the table below. | Review |
| API-20 | A `message` MUST be readable by a person and MUST NOT carry secrets, personal data or internal details. | Review |
| API-21 | `traceId` MUST be the request ID of SVC-08, so a caller can quote it when reporting a failure. | Review |

| Status | When |
|---|---|
| `400` | The request breaks the contract: a missing field, a wrong format, a value out of its limits |
| `401` | The caller has no valid identity |
| `403` | The caller has an identity but no right to the operation |
| `404` | The resource does not exist, or is not visible to the caller |
| `409` | The request conflicts with the current state, as in `SECTION_FULL` |
| `500` | An infrastructure failure the caller cannot fix |
| `503` | A dependency is unavailable and the caller MAY retry later |

## Compatibility

| ID | Rule | Check |
|---|---|---|
| API-22 | A contract MUST change by addition only: a new optional field, a new optional parameter, a new operation or a new error code. | Review |
| API-23 | A breaking change MUST be declared with `!` and a `BREAKING CHANGE:` footer. Removing or renaming a field, changing its type or meaning, making an input required, or changing a status is breaking. | Review |
| API-24 | A breaking change to a contract that a client outside this repository uses MUST be published under a new version prefix, as in `/api/v2/`, while the old version keeps working. | Review |
| API-25 | A schema name MUST NOT carry a version; the version lives only in the path. | Review |
