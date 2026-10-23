# Coding Standard

## Table of contents
- [Scope](#scope)
- [Principles](#principles)
- [Naming](#naming)
- [Functions](#functions)
- [Classes and modules](#classes-and-modules)
- [Errors](#errors)
- [Comments](#comments)
- [Logging](#logging)
- [Configuration and secrets](#configuration-and-secrets)
- [Dependencies](#dependencies)
- [Technical debt](#technical-debt)

---

## Scope

This standard applies to all code in the repository, in every language. Rules use **MUST** and **MUST NOT** for hard requirements and **SHOULD** for strong defaults that need a stated reason to break.

Related standards:
- [java.md](./java.md) - Java and Spring specifics
- [testing.md](./testing.md) - Test strategy and rules
- [governance.md](./governance.md) - Architectural invariants
- [CONTRIBUTING.md](../../CONTRIBUTING.md) - Commits, branches and pull requests

---

## Principles

- **Readable first.** Code is read far more often than written. Optimise for the next reader.
- **Explicit over clever.** No hidden side effects, no magic values, no surprising control flow.
- **Fail fast.** Reject invalid input and impossible states at the earliest point, loudly.
- **One way to do a thing.** Follow the existing pattern; change the pattern everywhere or not at all.
- **No debt.** Unfinished work is not merged. See [Technical debt](#technical-debt).

---

## Naming

- Names MUST state intent in domain terms: `reserveSeat`, `SeatInventory`, `holdExpiresAt`.
- Names MUST NOT describe mechanics or be generic: `process`, `handle`, `data`, `info`, `Manager`, `Helper`, `Util`, `Common`.
- Abbreviations MUST NOT be used, except `id`, `url`, `http`, `api`, `dto`.
- Booleans MUST read as questions: `isFull`, `hasExpired`, `canEnroll`.
- Units MUST be in the name when the type does not carry them: `timeoutMillis`, `holdDurationSeconds`. Prefer types that carry units (`Duration`, `Instant`).
- One concept, one word, across the whole repository. A seat that is held is a `hold`, never also a `lock` or a `booking`.

---

## Functions

| Limit | Value |
|---|---|
| Lines per function | 30 |
| Parameters | 4 (group more into a value object) |
| Nesting depth | 3 |
| Cyclomatic complexity | 10 |
| Line length | 120 characters |

- A function MUST do one thing at one level of abstraction.
- A function MUST NOT take boolean flag parameters. Split it into two functions.
- Guard clauses and early returns MUST be used instead of nested conditionals.
- Nested ternary expressions MUST NOT be used.
- A function MUST NOT both change state and return a computed answer, except for well-known patterns such as "create and return the ID".

---

## Classes and modules

| Limit | Value |
|---|---|
| Lines per file | 300 |
| Public methods per class | 10 |

- A class MUST have one reason to change.
- State MUST be immutable by default. Mutable state changes only through intention-revealing methods, never setters.
- Objects MUST be valid from construction. Constructors validate every argument.
- Dependencies MUST be passed in through constructors. No global state, no service locators.
- Visibility MUST be the smallest that works.

---

## Errors

- Invalid input MUST be rejected at the boundary that receives it, with a specific error code.
- Exceptions MUST NOT be swallowed. Catch only what you can handle; let the rest reach the boundary handler.
- Business errors and infrastructure errors MUST be distinct types, each with a stable, machine-readable code.
- Exceptions MUST NOT be used for normal control flow.
- Error messages MUST carry the context needed to act (identifiers, state) and MUST NOT carry secrets or personal data.
- `null` MUST NOT be used to signal failure or absence across a public boundary.

---

## Comments

The default is **no comment**. Before writing one, rename or extract until the code explains itself.

A comment is allowed only to:
1. Explain **why**: a decision, constraint or trade-off the code cannot express.
2. Warn about a non-obvious consequence.
3. Reference an ADR, issue or specification: `// See ADR-0004 for why holds expire in the database.`
4. Document a public API: ports, use cases, contracts.

Form:
- English, complete sentences, present tense.
- At most **3 lines** for a line or block comment.
- A documentation comment starts with a one-sentence summary and is at most **6 lines** before its tags.

Comments MUST NOT contain:
- `TODO`, `FIXME`, `XXX` or `HACK` markers
- commented-out code
- a restatement of what the code does
- section banners or separators (`// -----`)
- step narration (`// Step 1: validate`)
- author names, dates or change history
- jokes, emoticons, slang or vague words ("magic", "stuff", "should work")

Examples:

```java
// Bad: restates the code.
// Increment the counter.
reservedSeats++;

// Bad: marker, belongs in an issue.
// TODO: handle expired holds

// Good: explains a constraint the code cannot show.
// Holds expire in the database, not in memory, so a restarted instance never resurrects a released seat.
```

---

## Logging

- Logs MUST be structured (JSON) and written through the service's logger, never `System.out`.
- Messages MUST use placeholders, never string concatenation.
- Every log line about a request or event MUST carry its identifiers (`traceId`, `correlationId`, `eventId`).
- Passwords, tokens, keys and personal data MUST NOT be logged.
- `ERROR` MUST mean that someone has to act. Expected failures are `WARN` or lower.

---

## Configuration and secrets

- Hosts, ports, credentials, timeouts and limits MUST come from configuration, never literals in code.
- Every environment variable a service reads MUST be listed in its `.env.example` with a safe local default or an empty value.
- Secrets MUST NOT be committed. `.env` files and private keys are ignored by Git.

---

## Dependencies

- Dependency versions MUST be managed in one place per language (`platform/<lang>/`).
- A new dependency MUST have a stated reason in the pull request, and MUST NOT duplicate one already in use.
- Unused dependencies MUST be removed.

---

## Technical debt

Nothing unfinished enters the main branch.

- `TODO`, `FIXME` and similar markers MUST NOT appear in code, configuration or documentation. Open work is a [GitHub issue](https://github.com/phuck40clnq/slotgate/issues).
- Empty stubs, placeholder files and unused classes MUST NOT be committed. Something not built yet does not exist in the tree.
- Dead code and commented-out code MUST be deleted. Git keeps the history.
- A change MUST remove what it replaces: classes, configuration keys, dependencies, files and links.
- Suppressing a check (`@SuppressWarnings`, lint ignores) MUST name the check and give the reason on the same line.
- Existing debt is fixed in its own change, linked to its issue, never mixed into feature work.
