# PostgreSQL – Local Development

Services run against a PostgreSQL server installed on the host. There is no container here on purpose: a local server costs far less memory and CPU than a Docker VM on a developer machine. Docker remains the choice for CI and deployment.

## Set up

Run once, as a superuser of the local server:

```bash
psql -d postgres -f infrastructure/postgres/create-service-databases.sql
```

| Service | Database | Role | Password |
|---|---|---|---|
| auth-service | `slotgate_auth` | `slotgate_auth` | `slotgate_auth` |
| user-service | `slotgate_user` | `slotgate_user` | `slotgate_user` |

These credentials are for local development and match the defaults in each service's `application-local.yml`.

Each role reaches its own database only, following the [data ownership rules](../../docs/rules/governance.md#data-ownership). Names carry the `slotgate_` prefix so they do not collide with other projects on the same server.

## Add a service

Add its role and database to `create-service-databases.sql`, then run the new statements once with `psql -d postgres -c "..."`.

## Reset

This deletes all local data of these services:

```bash
psql -d postgres \
  -c "DROP DATABASE slotgate_auth" -c "DROP ROLE slotgate_auth" \
  -c "DROP DATABASE slotgate_user" -c "DROP ROLE slotgate_user"
```

Then run the set up command again.
