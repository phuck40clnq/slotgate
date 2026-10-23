# Error Contract

This folder defines the standard error response format used by all HTTP APIs exposed through the gateway.

## Rules
- All error **MUST** follow ErrorResponse schema
- `code` is stable and machine-readable
- `message` is human-readable
- `traceId` is optional

## Ownership
- Gateway enforces shape
- Services define business error codes
