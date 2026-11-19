# Incidents

How an incident record is written, for anyone who meets a failure nobody predicted while building, testing or deliberately breaking Slot Gate. Rules use the prefix `INC`; when a record is written follows DOC-16, and its file name DOC-49.

## Rules

| ID | Rule | Check |
|---|---|---|
| INC-01 | A record MUST be written for every unpredicted failure: a planned break that behaved differently, a test failing for an unknown reason, or a run misbehaving under load. | Review |
| INC-02 | A failure with a known, trivial cause, such as a missing environment variable, MUST NOT get a record. | Review |
| INC-03 | The status MUST move through `investigating`, `identified`, `resolved` and `reviewed`, in that order. | Review |
| INC-04 | A record MUST reach `reviewed` only when its lessons are written and every follow-up action is an issue linked from it. | Review |
| INC-05 | Evidence MUST be pasted verbatim: logs, trace IDs, event IDs, metrics and commands. | Review |
| INC-06 | The root cause MUST be the underlying cause, not the trigger, and MUST be confirmed by a row of the investigation table. | Review |

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
| Related | #issue, ADR-NNNN |

## Summary

Two or three sentences: what happened, its impact, the cause and the fix once known.

## Prediction

For a planned break only: what was expected and why.

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

The fix, its pull request, and before and after measurements.

## Lessons

What worked and what to change, with every follow-up action as an issue.
```
