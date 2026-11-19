# infrastructure/kafka

Kafka, Schema Registry and ZooKeeper for local work, run with Docker Compose. For anyone who runs a service that publishes or consumes events.

## Why it exists

`auth-service` publishes and `user-service` consumes events, so registration and sign-in need a broker and a schema registry. They run in containers here, because they are not installed on the host like PostgreSQL.

## Configuration

| Component | Image | Address |
|---|---|---|
| Kafka | `confluentinc/cp-kafka:7.5.0` | `localhost:9092` |
| Schema Registry | `confluentinc/cp-schema-registry:7.5.0` | `http://localhost:8081` |
| ZooKeeper | `confluentinc/cp-zookeeper:7.5.0` | `localhost:2181` |

## Run

| Task | Command |
|---|---|
| Start | `docker compose -f infrastructure/kafka/docker-compose.yml up -d` |
| List the containers | `docker compose -f infrastructure/kafka/docker-compose.yml ps` |
| Follow the logs | `docker compose -f infrastructure/kafka/docker-compose.yml logs -f` |
| Stop | `docker compose -f infrastructure/kafka/docker-compose.yml down` |
| Stop and delete all topics and schemas | `docker compose -f infrastructure/kafka/docker-compose.yml down -v` |

An older Docker installation uses `docker-compose` with a hyphen instead of `docker compose`.
