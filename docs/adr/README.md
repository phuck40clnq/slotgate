# Architecture decision records

How an architecture decision record (ADR) is written, for anyone who records a decision. Rules use the prefix `ADR`; when an ADR is needed follows DOC-16, its file name DOC-48, and its immutability DOC-10.

## Template

```markdown
# ADR-NNNN: <The decision in the imperative>

## Status

Proposed

## Context

<The problem, its constraints, and the measurement or prediction that forces a decision.>

## Options

<Each option considered, with the reason it was chosen or rejected.>

## Decision

<What is decided, in the imperative.>

## Consequences

<What gets better, what gets worse, and what to watch.>
```

## Rules

| ID | Rule | Check |
|---|---|---|
| ADR-01 | An ADR MUST follow the template above, with every section filled. | Review |
| ADR-02 | The title MUST state the decision in the imperative, such as `Claim seats with an atomic conditional update`. | Review |
| ADR-03 | The context MUST state the problem, its constraints, and the measurement or prediction behind the decision. | Review |
| ADR-04 | The options MUST list at least two options, each with the reason it was chosen or rejected. | Review |
| ADR-05 | The consequences MUST name what gets worse, not only what gets better. | Review |
| ADR-06 | The status MUST be `Proposed`, `Accepted` or `Superseded by ADR-NNNN`, and only an accepted ADR binds. | Review |
| ADR-07 | An ADR MUST be accepted by the project owner before the code it decides is merged. | Review |
| ADR-08 | An ADR that supersedes another MUST link to it, and the status of the superseded ADR MUST link back. | Review |
