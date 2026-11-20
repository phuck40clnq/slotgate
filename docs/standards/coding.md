# Coding standard

How code is written in every language of Slot Gate, for everyone who writes or reviews it. Rules use the prefix `CODE`, and `docs/standards/<language>.md` adds the specifics of each language.

## Principles

- Readable first, because code is read far more often than it is written
- Explicit over clever: no hidden side effects, magic values or surprising control flow
- Fail fast: invalid input and impossible states are rejected at the earliest point
- One way to do a thing: a pattern changes everywhere or nowhere

## Naming

| ID | Rule | Check |
|---|---|---|
| CODE-01 | A name MUST state intent in domain terms, as in `reserveSeat`, `SeatInventory` and `holdExpiresAt`. | Review |
| CODE-02 | A name MUST NOT describe mechanics or be generic, as in `process`, `handle`, `data`, `info`, `Manager`, `Helper`, `Util` and `Common`. | Review |
| CODE-03 | A name MUST NOT abbreviate, except `id`, `url`, `http`, `api` and `dto`. | Review |
| CODE-04 | A boolean MUST read as a question, as in `isFull`, `hasExpired` and `canEnroll`. | Review |
| CODE-05 | A name MUST carry its unit when its type does not, as in `timeoutMillis`. | Review |
| CODE-06 | A concept MUST keep the name that [product](../core/product.md) gives it: a held seat is a `hold`, never a `lock` or a `booking`. | Review |
| CODE-07 | A type MUST carry the suffix of its role in the table below. | Review |

| Role | Suffix |
|---|---|
| Inbound port | `UseCase` |
| Use case implementation | `Service` |
| Outbound port | `Port` |
| Adapter | `Adapter` |
| Application input and output | `Command`, `Result` |
| Web input and output | `Request`, `Response` |

## Functions

| ID | Rule | Check |
|---|---|---|
| CODE-08 | A function MUST stay within the limits below, and MUST group extra parameters into a value object. | Review |
| CODE-09 | A function MUST do one thing at one level of abstraction; steps that need a comment to separate them MUST become functions. | Review |
| CODE-10 | A function MUST NOT take a boolean flag parameter; it MUST be split into two functions instead. | Review |
| CODE-11 | Guard clauses and early returns MUST replace nested conditionals, and ternary expressions MUST NOT be nested. | Review |
| CODE-12 | A function MUST NOT both change state and return a computed answer, except to create something and return its ID. | Review |

| Limit | Value |
|---|---|
| Lines per function | 30 |
| Parameters | 4 |
| Nesting depth | 3 |
| Cyclomatic complexity | 10 |
| Characters per line of code | 120 |

## Types and modules

| ID | Rule | Check |
|---|---|---|
| CODE-13 | A file MUST stay within 300 lines and a type within 10 public methods, and each MUST have one reason to change. | Review |
| CODE-14 | State MUST be immutable by default; mutable state MUST change only through methods named after the intent, never through setters. | Review |
| CODE-15 | An object MUST be valid from construction, because its constructor validates every argument. | Review |
| CODE-16 | Dependencies MUST arrive through constructors; global state and service locators MUST NOT be used. | Review |
| CODE-17 | Visibility MUST be the narrowest that works. | Review |

## Errors

| ID | Rule | Check |
|---|---|---|
| CODE-18 | Input MUST be validated once, at the boundary that receives it, with a specific error code; code inside the boundary trusts its types. | Review |
| CODE-19 | An exception MUST NOT be swallowed: code catches only what it can handle, and the rest reaches the boundary handler. | Review |
| CODE-20 | Business errors and infrastructure errors MUST be distinct types, each with a stable code from the error code list of the service. | Review |
| CODE-21 | Exceptions MUST NOT drive normal control flow. | Review |
| CODE-22 | An error message MUST carry the identifiers and state needed to act, and MUST NOT carry secrets or personal data. | Review |
| CODE-23 | Absence or failure MUST NOT be signalled with a null value across a public boundary. | Review |

## Comments

The default is no comment: code is renamed or extracted until it explains itself.

| ID | Rule | Check |
|---|---|---|
| CODE-24 | A comment MUST explain why, warn about a consequence the code cannot show, reference an ADR or issue, or document a public port, use case or contract. | Review |
| CODE-25 | A comment MUST be in English, in complete sentences and the present tense, within 3 lines. | Review |
| CODE-26 | A documentation comment MUST open with a one-sentence summary and stay within 6 lines before its tags. | Review |
| CODE-27 | A comment MUST NOT contain `TODO`, `FIXME`, `XXX`, `HACK` or commented-out code, because open work is an issue and history is in git. | Review |
| CODE-28 | A comment MUST NOT restate the code, draw banners, narrate steps, name authors or dates, or use jokes, slang or vague words. | Review |

Example, in Java:

```java
// Bad: restates the code.
// Increment the counter.
reservedSeats++;

// Good: explains a constraint the code cannot show.
// Holds expire in the database, not in memory, so a restarted instance never resurrects a released seat.
```

## Logging

| ID | Rule | Check |
|---|---|---|
| CODE-29 | A log line MUST go through the structured logger of the service, in the format of [the service standard](services.md). | Review |
| CODE-30 | A log message MUST use placeholders or structured arguments, never concatenation, and every argument MUST have a placeholder, because one without is silently dropped. | Review |
| CODE-31 | A log line about a request or an event MUST carry its identifiers, such as `traceId`, `correlationId`, `eventId` and the tenant. | Review |
| CODE-32 | Passwords, tokens, keys and personal data MUST NOT be logged. | Review |
| CODE-33 | `ERROR` MUST mean that someone has to act; an expected failure MUST be `WARN` or lower. | Review |

## Configuration and dependencies

| ID | Rule | Check |
|---|---|---|
| CODE-34 | Hosts, ports, credentials, timeouts and limits MUST come from configuration with a safe local default, never from literals in code. | Review |
| CODE-35 | Dependency versions MUST live in one place per language, `platform/<language>/`. | Review |
| CODE-36 | A new dependency MUST state its reason in the pull request and MUST NOT duplicate one already in use. | Review |

## Debt

| ID | Rule | Check |
|---|---|---|
| CODE-37 | Code and configuration MUST NOT contain `TODO` or `FIXME` markers; open work is an issue. | `git grep` |
| CODE-38 | Empty stubs, placeholder files, unused code, unused dependencies and commented-out code MUST NOT be committed, because what is not built does not exist. | Review |
| CODE-39 | A change MUST remove what it replaces: types, configuration keys, dependencies, files and links. | Review |
| CODE-40 | A suppressed check MUST name the check and give its reason on the same line. | Review |
| CODE-41 | Existing debt MUST be fixed in its own change, never mixed into feature work. | Review |
| CODE-42 | Code that breaks this standard MUST NOT be copied as a pattern. | Review |
