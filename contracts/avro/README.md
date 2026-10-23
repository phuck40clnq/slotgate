# Avro Event Contract Policy

Event contracts define business events used for asynchronous communication between services.

## Principles
***Event contracts are***:
- **Shared across services**
- **Owned by business domains**
- **Technology-agnostic**
- **Stable long-term**
- **Versioned ONLY via Schema Registry**
### View full example at [here](auth/events/UserRegistered.avsc)
---

### Summary of rules:
- [Naming File Rule](#rule-1-file-name): `PascalCase.avsc`
- [Naming Record Rule](#rule-2-avro-record-naming): `PascalCase`
- [Field](#rule-3-fields): `camelCase`
- [Field Attributes](#rule-4-field-attributes): `doc`, `default`, `logicalType`
- [Topic](#rule-5-kafka-topic): `kebab-case`
- [Subject](#rule-6-schema-subject): `<topic>-value`
- [Version](#rule-7-versioning): **`Schema Registry ONLY`**
---

### Rule 1: File name
- Use `PascalCase.avsc`
- File name represents the **event name**
- MUST be immutable once published
- *In this case*, I chose it the same with `record name`

Example:
```
UserRegistered.avsc
```
---

### Rule 2: Avro record naming
- Record name MUST be `PascalCase`
- One record per PascalCase.avsc
- Record represents a **business event**

Example:
```json
{
  "type": "record",
  "name": "UserRegistered"
}
```
---

### Rule 3: Fields
- Field names MUST be camelCase
- Fields represent **facts that happened**
- Fields MUST NOT represent derived or current state

#### Required field:
An event contract avro MUST define a SINGLE avro `record` with the following structure:
- `type` MUST be `record`
- `name` MUST be `PascalCase`
- `namespace` MUST represent business ownership
    
    Format: `<org>.<context>.events`

Example:
``` json
{
  "type": "record",
  "name": "UserRegistered",
  "namespace": "com.example.user.events"
}
```

**Optional field**:
- MUST be union with `null`
- `null` MUST be the first type
- MUST define `default: null`
``` json
{
  "name": "referralCode",
  "type": ["null", "string"],
  "default": null
}
```
---

### Rule 4: Field attributes
- `doc` SHOULD be present for business clarity
- `default` MUST be provided for optional fields
- `logicalType` MUST be used for timestamps
Standard fields:
``` json
{
  "name": "eventId",
  "type": "string",
  "doc": "Unique identifier of this event"
},
{
  "name": "occurredAt",
  "type": {
    "type": "long",
    "logicalType": "timestamp-millis"
  },
  "doc": "Event occurrence time in UTC"
},
```
---

### Rule 5: Kafka topic
- Use `kebab-case` notation
- Topic name represents the **business event**

Format:
```
<context>-<aggregate>-events
```

Example:
```
auth-user-events
```
---

### Rule 6: Schema subject
- Subject MUST be `<topic>-value`
- One subject per event type

Example:
```
auth-user-registered-value
```
---

### Rule 7: Versioning
> **Versioning is handled ONLY by Schema Registry**
- *DO NOT* version in:
    - file name
    - record name
    - namespace
