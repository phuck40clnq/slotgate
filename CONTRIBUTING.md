# Contributing

## Table of contents
- [Workflow](#workflow)
- [Issues](#issues)
- [Branches](#branches)
- [Commits](#commits)
- [Pull requests](#pull-requests)
- [Standards](#standards)

---

## Workflow

Every change follows the same path:

1. **Issue.** The change starts from an issue that states the problem and its acceptance criteria.
2. **Branch.** A short-lived branch from the latest `main`.
3. **Commits.** Small, logical commits in the Conventional Commits format.
4. **Pull request.** One pull request per issue, filled in from the template.
5. **Checks.** CI is green and the Definition of Done in the template is met.
6. **Merge.** Squash merge into `main`, then delete the branch.

`main` is protected: no direct pushes, no force pushes, linear history, and all CI checks must pass.

---

## Issues

- Every change MUST start from an issue: https://github.com/phuck40clnq/slotgate/issues
- The title is a specific, imperative sentence: `Reject duplicate UserRegistered events in user-service`.
- The body states the context, the acceptance criteria as a checklist, what is out of scope, and references (ADR, PRD section, logs).
- Labels:
  - **type**: `feature`, `bug`, `debt`, `docs`, `chore`
  - **area**: `gateway`, `auth`, `user`, `contracts`, `platform`, `infra`, `ci`, `frontend`
  - **phase**: `phase-1` to `phase-4`, matching the roadmap in the [README](README.md#roadmap)
- Debt found while working on something else MUST become its own issue. It is never fixed silently inside an unrelated change.

---

## Branches

- Name: `<type>/<issue>-<short-name>` in kebab case, for example `feat/12-seat-inventory` or `fix/31-duplicate-user-registered`.
- Branch from the latest `main`. Update by rebasing onto `main`; never merge `main` into a branch.
- A branch SHOULD live less than a week. Split the work if it takes longer.

---

## Commits

Commits follow [Conventional Commits 1.0.0](https://www.conventionalcommits.org/en/v1.0.0/):

```text
<type>(<scope>): <summary>

<body>

<footer>
```

| Type | Use for |
|---|---|
| `feat` | A new behaviour visible to users or other services |
| `fix` | A defect fix |
| `refactor` | A code change that neither fixes a defect nor adds behaviour |
| `perf` | A performance improvement with a measurement |
| `test` | Adding or correcting tests only |
| `docs` | Documentation only |
| `build` | Build system, dependencies, platform POMs |
| `ci` | CI and CD configuration |
| `chore` | Maintenance that fits none of the above |

Rules:
- **Scope** is the service or area: `gateway`, `auth`, `user`, `test-service`, `contracts`, `platform`, `templates`, `infra`, `frontend`. Omit it only for repository-wide changes.
- **Summary** is imperative, starts lowercase, has no trailing period, and the whole first line is at most 72 characters.
- **Body** explains why the change is needed and what it changes at a high level, wrapped at 72 characters. The diff already shows how.
- **Footer** links issues (`Refs #12`, `Closes #12`). A breaking change to a contract adds `!` after the type and a `BREAKING CHANGE:` footer.
- Every commit MUST build and pass its tests, so history stays bisectable.
- Commits such as `wip`, `update`, `fix typo` or `misc` MUST NOT reach `main`. Squash or reword them before review.

Examples:

```text
Good:  feat(user): ignore duplicate UserRegistered events
Good:  fix(gateway): return 429 when the rate limit is exceeded
Good:  build(platform): add Spotless with palantir-java-format

Bad:   Fixed bug                          (no type, past tense, vague)
Bad:   feat: Added new feature.           (past tense, capitalised, period, vague)
Bad:   chore(user): update stuff and fix tests and refactor handler   (several changes)
```

---

## Pull requests

- One pull request per issue, one purpose per pull request.
- The title MUST follow the commit format above; it becomes the squash commit on `main`.
- The description MUST be filled in from the [pull request template](.github/pull_request_template.md). Every item of its Definition of Done MUST be checked or explained.
- A pull request SHOULD change at most 400 lines, excluding generated files. Larger work is split into a stack of smaller pull requests. Purely mechanical changes (renames, formatting) are the only exception and are stated as such.
- A contract change MUST show its compatibility check and update every consumer in the same pull request or a linked one.
- An architectural decision MUST include its ADR in [docs/adr/](docs/adr/overview.md) in the same pull request.
- All CI checks MUST pass. Checks are never skipped, disabled or retried until green.
- Before asking for review, the author reads the full diff on GitHub as a reviewer would.
- Merge with **squash** only, then delete the branch.

---

## Standards

Code in a pull request follows:
- [Coding standard](docs/rules/coding.md)
- [Java standard](docs/rules/java.md)
- [Testing standard](docs/rules/testing.md)
- [Governance rules](docs/rules/governance.md)
- [Contract policies](contracts/README.md)

Setup and commands are in the [README](README.md#getting-started).
