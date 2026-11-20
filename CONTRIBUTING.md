# Contributing

How a change travels from a problem to `main`, for everyone who changes the repository. Rules use the prefix `FLOW`; what a change contains follows [`docs/standards/`](docs/standards/) and the [documentation standard](docs/README.md).

## Workflow

1. State the problem in an issue, or in the pull request description.
2. Create a branch from the remote `main`.
3. Commit small, logical steps that each build and pass their tests.
4. Open a pull request from the template.
5. Get every check green and every Definition of Done item checked or explained.
6. Squash merge into `main`, then delete the branch.

## Issues

| ID | Rule | Check |
|---|---|---|
| FLOW-01 | A change MUST start from an issue, or from a pull request description that states the problem and its acceptance criteria. | Review |
| FLOW-02 | An issue title MUST be a specific sentence in the imperative, as in `Reject duplicate UserRegistered events in user-service`. | Review |
| FLOW-03 | An issue body MUST state the context, the acceptance criteria as a checklist, what is out of scope, and its references. | Review |
| FLOW-04 | An issue MUST carry one label of each group in the table below, as the issue forms in `.github/ISSUE_TEMPLATE/` offer them. | Review |
| FLOW-05 | Debt found during other work MUST become its own issue, and MUST NOT be fixed inside the unrelated change. | Review |

| Group | Labels |
|---|---|
| Type | `feature`, `bug`, `debt`, `docs`, `chore` |
| Area | `gateway`, `auth`, `user`, `contracts`, `platform`, `infra`, `ci`, `frontend` |
| Phase | `phase-1` to `phase-4`, the phases of the [product requirements](docs/prd-2026-10-17.pdf) |

## Branches

| ID | Rule | Check |
|---|---|---|
| FLOW-06 | A branch MUST be created from the remote `main` and named `<type>/<short-name>`, or `<type>/<issue>-<short-name>` when an issue exists. | Review |
| FLOW-07 | Work that needs an unmerged branch MUST start from that branch, and its pull request MUST name the pull request it builds on. | Review |
| FLOW-08 | A branch MUST be updated by rebasing onto `main`, never by merging `main` into it. | Review |
| FLOW-09 | Nobody MAY commit or push to `main` directly or force-push it; changes reach it only through squash-merged pull requests. | Branch protection |

## Commits

A commit message follows [Conventional Commits 1.0.0](https://www.conventionalcommits.org/en/v1.0.0/): `<type>(<scope>): <summary>`, with an optional body and footer.

| ID | Rule | Check |
|---|---|---|
| FLOW-10 | The type MUST be one of the table below. | Review |
| FLOW-11 | The scope MUST be the service without `-service`, as in `seat-inventory`, or the area, as in `contracts`, `platform` or `adr`; a change across the repository omits it. | Review |
| FLOW-12 | The summary MUST be one sentence in the imperative and lower case, without a final period, and the first line MUST NOT exceed 72 characters. | Review |
| FLOW-13 | A body MAY add one short sentence on why, when the summary cannot say it. | Review |
| FLOW-14 | A breaking change to a contract, an API, an event or a data format MUST add `!` after the scope and a one-line `BREAKING CHANGE:` footer. | Review |
| FLOW-15 | The only trailers MUST be `Refs #N` or `Closes #N` for an existing issue and `BREAKING CHANGE:`; authorship and tool attributions MUST NOT be added. | Review |
| FLOW-16 | Every commit MUST build and pass its tests, so that history stays bisectable. | `./mvnw verify` |
| FLOW-17 | A commit MUST hold one logical change; messages such as `wip`, `update` or `fix typo` MUST NOT reach `main`. | Review |

| Type | Use for |
|---|---|
| `feat` | A behaviour visible to users or other services |
| `fix` | A defect fix |
| `refactor` | A change that neither fixes a defect nor adds behaviour |
| `perf` | A performance gain, with the numbers CORE-24 asks for |
| `test` | Tests only |
| `docs` | Documentation only |
| `build` | Build system, dependencies, platform files |
| `ci` | Continuous integration and delivery |
| `chore` | Maintenance that fits none of the above |

Examples:

```text
Good: feat(seat-inventory): list sections with a code and a title
Good: fix(gateway): return 429 when the rate limit is exceeded
Bad:  Fixed bug                         no type, past tense, vague
Bad:  feat: Added new feature.          past tense, capital letter, period
Bad:  chore(user): update stuff and refactor handler   several changes
```

## Pull requests

| ID | Rule | Check |
|---|---|---|
| FLOW-18 | A pull request MUST have one purpose, and its title MUST follow the commit format, because it becomes the squash commit. | Review |
| FLOW-19 | The description MUST be filled in from the [pull request template](.github/pull_request_template.md), with every Definition of Done item checked or explained. | Review |
| FLOW-20 | A pull request MUST change at most 400 lines excluding generated files, or its summary MUST explain why; larger work is split into stacked pull requests. | Review |
| FLOW-21 | A contract change MUST show its compatibility check and update every consumer in the same pull request or a linked one. | Review |
| FLOW-22 | A decision that DOC-16 assigns to an ADR MUST bring its ADR in the same pull request. | Review |
| FLOW-23 | Every check MUST pass, and no check MAY be skipped, disabled or rerun until it passes. | Continuous integration |
| FLOW-24 | The author MUST read the full diff on GitHub, as a reviewer would, before asking for review. | Review |
