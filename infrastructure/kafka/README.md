# Infra – Local Development

## Description about docker
`docker-compose` and `docker compose` both do the same but the first one is for old version and the second is for latest

***Notes: I use an old version of docker***

## Start infra
`docker-compose up -d`

## Stop infra
`docker-compose down`

## View logs
`docker-compose logs -f`

## Reset all data *(careful)*
`docker-compose down -v`

## List all active container
`docker-compose ps`

## Services
- Kafka: http://localhost:9092
- Schema Registry: http://localhost:8081
- Zookeeper: http://localhost:2181
