# Contracts

The HTTP and event contracts between the services of Slot Gate and their clients, for anyone who changes an API or an event. Code follows these files, never the reverse, as CORE-08 and CORE-09 require.

## Why it exists

Services share no code and may use different languages, so these language-neutral files are the only shared definition of what they exchange. A service generates or checks its types against them.

## API and events

| Folder | Holds | Rules |
|---|---|---|
| `openapi/<service>/` | One OpenAPI 3 file per service | [API standard](../docs/standards/api.md) |
| `openapi/error/` | The `ErrorResponse` that every error response uses | API-17 to API-21 |
| `openapi/headers/` | The custom request headers that contracts reference, such as `X-Internal-User-Id` | API-15 and API-16 |
| `avro/<owning service>/events/` | One Avro schema per event | [Event standard](../docs/standards/events.md) |

## Details

A contract changes in this order:

1. Change the file here, in the same pull request as the code and before it.
2. Check the change against the compatibility rules: API-22 to API-25, or EVT-14 and EVT-15.
3. Mark a breaking change with `!` and a `BREAKING CHANGE:` footer in the commit message.
4. Copy a changed schema unchanged into every service that keeps a copy, as EVT-09 requires.
5. Change the code that produces and consumes it.
