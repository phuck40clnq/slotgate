# ADR-0003: Use JWT with Asymmetric Signing (RSA)

## Status
Accepted

## Context

The system requires stateless authentication
across multiple services.

Requirements:
- Secure token verification
- No shared secret between all services
- Support for distributed validation
- Scalability without central session storage

## Decision

Authentication will use JWT tokens
signed with asymmetric cryptography (RSA).

- The authentication service signs tokens using a private key.
- Other services verify tokens using the public key.
- No shared secret is distributed across services.
- Services do not store session state.

## Consequences

### Positive

- Stateless authentication
- Scalable verification
- No shared secret across services
- Clear trust boundary

### Negative

- Key rotation complexity
- Token revocation is non-trivial
- Requires secure key management
