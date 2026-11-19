# infrastructure/postgres

The setup of the local PostgreSQL server: one database and one role per service. For anyone who runs the services on a developer machine.

## Why it exists

Services run against a PostgreSQL server installed on the host, because it costs far less memory and CPU than a Docker virtual machine on a developer machine. Continuous integration and deployment use Docker. One role per database keeps CORE-10: each service role reaches its own database only.

## Configuration

These credentials are for local work only, and match the defaults of each service's `local` profile:

| Service | Database | Role | Password |
|---|---|---|---|
| `auth-service` | `slotgate_auth` | `slotgate_auth` | `slotgate_auth` |
| `user-service` | `slotgate_user` | `slotgate_user` | `slotgate_user` |
| `seat-inventory-service` | `slotgate_seat_inventory` | `slotgate_seat_inventory` | `slotgate_seat_inventory` |

Names carry the `slotgate_` prefix, so they do not collide with other projects on the same server.

## Run

Run once, as a superuser of the local server:

```bash
psql -d postgres -f infrastructure/postgres/create-service-databases.sql
```

## Details

- A new service adds its role and database as step 5 of the [guide](../../docs/guides/add-a-service.md) shows.
- This deletes all local data of these services; the setup command then creates them again:

```bash
psql -d postgres -c "DROP DATABASE slotgate_auth" -c "DROP ROLE slotgate_auth" -c "DROP DATABASE slotgate_user" -c "DROP ROLE slotgate_user" -c "DROP DATABASE slotgate_seat_inventory" -c "DROP ROLE slotgate_seat_inventory"
```
