# Add a service

How to add a service to Slot Gate, from its contract to a route through the gateway, for anyone who creates one. The service must meet the [service standard](../standards/services.md), and a service in a new language first needs the documents that DOC-16 lists.

## Before you start

- A new service needs a reason under CORE-16: a measured target that an existing service violates, or an ADR that finds a boundary no service owns.
- Its language needs a template in `templates/`, as CORE-15 requires.

## Steps

1. Write its contracts first: `contracts/openapi/<name>/<name>.openapi.yaml`, and an Avro schema for each event it publishes.
2. Copy the template of its language, here the Java one:

   ```bash
   cp -R templates/spring-boot-service services/<name>-service
   ```

3. Replace every sample value of the template, as the [template README](../../templates/spring-boot-service/README.md) lists.
4. Take the next free port, and add the service to the component table of the root [`README.md`](../../README.md).
5. If the service keeps state, add its database and role to `infrastructure/postgres/create-service-databases.sql`, then create them:

   ```bash
   psql -d postgres -f infrastructure/postgres/create-service-databases.sql
   ```

6. Add its route to `services/gateway-service/src/main/resources/application.yml`:

   ```yaml
   - id: <name>-service
     uri: lb://<name>-service
     predicates:
       - Path=/api/<name>/**
     filters:
       - RewritePath=/api/<name>/(?<segment>.*), /api/v1/<name>/${segment}
   ```

7. Add its local instance to `application-local.yml` of the gateway:

   ```yaml
   <name>-service:
     - uri: http://localhost:<port>
   ```

8. Fill in its `.env.example` and its `README.md`, following SVC-05 and DOC-59.
9. Add it to the diagram and tables of the [architecture overview](../architecture/overview.md).
10. Build and test it:

    ```bash
    cd services/<name>-service
    ./mvnw verify
    ```
