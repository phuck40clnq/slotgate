# Event standard

How the events of Slot Gate are defined, named, published and consumed, for anyone who writes an event contract, a producer or a consumer. Rules use the prefix `EVT`, and the schemas live in `contracts/avro/`.

## Schemas

| ID | Rule | Check |
|---|---|---|
| EVT-01 | Every event MUST be one Avro record in `contracts/avro/<owning service>/events/<EventName>.avsc`. | Review |
| EVT-02 | The record and its file MUST be named after the event in PascalCase and the past tense, as in `UserRegistered`, and a published name MUST NOT change. | Review |
| EVT-03 | The namespace MUST be `com.example.<owning service>.events`. | Review |
| EVT-04 | Every event MUST carry `eventId`, a UUID string, and `occurredAt`, a `long` with the logical type `timestamp-millis` in UTC. | Review |
| EVT-05 | Every field MUST be camelCase and have a `doc` that says what fact it records. | Review |
| EVT-06 | A field MUST record a fact as it was when the event occurred, never a derived or current state. | Review |
| EVT-07 | An optional field MUST be a union with `null` first and `"default": null`. | Review |
| EVT-08 | An event MUST carry only the facts its consumers need, and MUST NOT expose the internal structures of its producer. | Review |
| EVT-09 | A service that keeps a copy of a schema MUST keep it identical to the file in `contracts/avro/`. | `diff` |

## Topics, keys and subjects

| ID | Rule | Check |
|---|---|---|
| EVT-10 | A topic MUST be named `<owning service>-<aggregate>-events` in kebab-case, as in `auth-user-events`. | Review |
| EVT-11 | The record key MUST be the identifier of the aggregate, so the events of one aggregate stay in order. | Review |
| EVT-12 | A schema MUST be registered under its full record name, as in `com.example.auth.events.UserRegistered`, so one topic MAY carry several event types. | Review |
| EVT-13 | A version MUST live only in Schema Registry, never in a file name, a record name or a namespace. | Review |

## Compatibility

| ID | Rule | Check |
|---|---|---|
| EVT-14 | A schema MUST change only in ways its current consumers can read: adding an optional field with a default, or adding a `doc`. | Review |
| EVT-15 | An incompatible change MUST be a new event with its own record name, published next to the old one until no consumer reads the old one. | Review |

## Publishing

| ID | Rule | Check |
|---|---|---|
| EVT-16 | An event MUST be published only by the service that owns the data it describes. | Review |
| EVT-17 | An event MUST state what happened and MUST NOT ask another service to act. | Review |
| EVT-18 | Changing state and publishing its event MUST be atomic through a transactional outbox. Until the outbox exists, every place that does both in two steps MUST be listed in an issue. | Review |

## Consuming

| ID | Rule | Check |
|---|---|---|
| EVT-19 | A consumer MUST record the `eventId` of each event it applies and skip an event whose `eventId` it has recorded, before changing any state. | Test |
| EVT-20 | A consumer MUST retry a failed event a bounded number of times with backoff, then send it to a dead-letter topic; it MUST NOT drop it or retry it forever. | Test |
| EVT-21 | A consumer MUST NOT assume that another service's data is current, because state spreads between services eventually. | Review |
| EVT-22 | A failed event flow MUST NOT fall back to a synchronous call to the producer. | Review |
