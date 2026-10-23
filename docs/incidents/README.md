# Incidents

## Table of contents
- [Scope](#scope)
- [When to write one](#when-to-write-one)
- [Naming and lifecycle](#naming-and-lifecycle)
- [Template](#template)

---

## Scope

This directory records unexpected failures found while building, testing or deliberately breaking the system, and the investigation that follows each one. These records are the raw material for root cause analysis and post-mortems (roadmap Phase 3).

---

## When to write one

- Write one for every failure you did not predict: a planned break that behaved differently, a test that fails for an unknown reason, a run that misbehaves under load.
- Write it **before** fixing. Evidence disappears once the cause is fixed.
- A failure with a known, trivial cause (a missing environment variable) is an issue, not an incident.
- Follow-up work is filed as issues and linked from the record.

---

## Naming and lifecycle

- File name: `YYYY-MM-DD-short-kebab-title.md`, using the detection date.
- Status moves through `investigating` → `identified` → `resolved` → `reviewed`. A record is `reviewed` once its lessons are written and its follow-up issues exist.
- Times are in UTC, matching the log timestamps.

---

## Template

```markdown
# <The symptom, for example: Seat count goes negative under concurrent expiry>

| Field | Value |
|---|---|
| Detected | YYYY-MM-DD HH:MM UTC |
| Status | investigating / identified / resolved / reviewed |
| Severity | critical / major / minor |
| Services | |
| Commit | |
| Related | #issue, ADR-000X |

## Summary
Two or three sentences: what happened, its impact, the cause and the fix once known.

## Prediction
For a planned break only: what you expected to happen and why.

## Timeline
| Time (UTC) | Event |
|---|---|

## Symptoms
What was observed, where, and how it was noticed.

## Evidence
Logs, trace IDs, event IDs, metrics and commands, pasted verbatim.

## Investigation
| # | Hypothesis | How it was tested | Result |
|---|---|---|---|

## Root cause
The underlying cause, not the trigger.

## Resolution
The fix, its pull request, and before/after measurements.

## Lessons
What worked, what to change. Every follow-up action is an issue.
```
