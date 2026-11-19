# Documentation standard

This standard decides which documents Slot Gate has, where each lives, when each is written and how each is written, and it binds every contributor, person or agent. Rules use MUST, MUST NOT and MAY as defined in [RFC 2119](https://www.rfc-editor.org/rfc/rfc2119), with the ID prefix `DOC`.

## Why documents exist

Slot Gate is a multi-tenant platform made of independent services, each written in the language that suits its job. The services share no code, so contracts and documents are what make them one system. Documents record what code cannot show:

- What the platform guarantees and must never violate
- How to build any part of it the same way, in any language
- Why it is built this way, and what happened when it failed

## How rules are written

Every binding document writes its rules the way this section does.

| ID | Rule | Check |
|---|---|---|
| DOC-01 | A rule MUST be one row of a table with the columns `ID`, `Rule` and `Check`. | Review |
| DOC-02 | A rule MUST use MUST, MUST NOT or MAY, and MUST NOT use SHOULD or RECOMMENDED, because a rule that may be skipped is not a rule. | Review |
| DOC-03 | A rule ID MUST be `<PREFIX>-<NN>`, with a prefix unique to its document and declared in the first paragraph of that document. | Review |
| DOC-04 | A rule ID MUST NOT be reused or renumbered, because reviews and pull requests cite it; a removed rule leaves a gap. | Review |
| DOC-05 | A rule MUST give its reason after "because", unless the rule itself makes the reason plain. | Review |
| DOC-06 | The `Check` column MUST name how the rule is verified: `Review`, a command, or a test or tool by name. A statement that cannot be checked MUST NOT be written as a rule. | Review |
| DOC-07 | A review comment about a broken rule MUST cite the rule ID. | Review |

## Kinds of document

| Kind | Where | Answers | Status |
|---|---|---|---|
| Core | `docs/core/` | What Slot Gate is, and what it must never violate | Binding |
| Standards | `docs/standards/` | How to build a part correctly | Binding |
| Contracts | `contracts/` | What services and clients exchange | Binding |
| Architecture decision records (ADRs) | `docs/adr/` | Why one option was chosen over the others | Record |
| Architecture | `docs/architecture/` | How the running system works now | Description |
| Component READMEs | `README.md` in the component's folder | What the component is, why it exists, how to run and test it | Description |
| Guides | `docs/guides/` | How to do one task from start to end | Description |
| Incidents | `docs/incidents/` | What failed unexpectedly, and why | Record |
| Weekly reports | `docs/weekly/` | What one week achieved | Record |
| Product requirements | `docs/prd-2026-10-17.pdf` | The scope first agreed with the supervisor | Record |

A component is a folder directly under `services/`, `platform/`, `templates/`, `frontend/` or `infrastructure/`, or `contracts/` itself. Binding documents decide, descriptions explain the system as it is, and records keep the facts and reasoning of their time.

| ID | Rule | Check |
|---|---|---|
| DOC-08 | A document MUST belong to exactly one kind and live where that kind lives. A document that fits no kind MUST NOT be written until this standard adds the kind. | Review |
| DOC-09 | When two documents conflict, core MUST prevail over standards, standards over contracts, and contracts over descriptions. The lower document MUST be fixed in the pull request that finds the conflict. | Review |
| DOC-10 | A record MUST NOT be edited once final, except its status line: an ADR MAY become `Superseded`, and an incident MAY move along its lifecycle. | Review |
| DOC-11 | API and event references MUST be rendered from `contracts/` and MUST NOT be written by hand. | Review |
| DOC-12 | A kind with a template MUST keep the template and the rules of that kind in the `README.md` of its folder, and nowhere else. | Review |
| DOC-13 | Every component MUST have a `README.md` that says what the component is, why it exists, and how to run and test it. | Review |
| DOC-14 | Core, contracts, architecture and language-neutral standards MUST NOT name a language, framework or build tool, except as a labelled example, because any service may use another language. | Review |
| DOC-15 | Language specifics MUST live only in `docs/standards/<language>.md`, `platform/<language>/` and `templates/<language>-service/`. | Review |

## When to write

| ID | Rule | Check |
|---|---|---|
| DOC-16 | Every event in the table below MUST come with the documents it lists, at the time it lists. | Review |

| Event | Documents | When |
|---|---|---|
| A change to what the platform guarantees, its scope or a core concept | An ADR, then `docs/core/` | Before the code |
| A choice between options that affects correctness, reliability, performance, security or more than one component | An ADR | Before the code |
| A new language | An ADR, `docs/standards/<language>.md`, `platform/<language>/README.md` and `templates/<language>-service/README.md` | Before the first service in it |
| Rules that apply across components and fit no existing standard | A new standard in `docs/standards/` | Before the code that follows them |
| A new or changed API or event | The contract in `contracts/` | Before the code |
| A new service | Its `README.md` from the template, `docs/architecture/overview.md` and the component table in the root `README.md` | With the code |
| A new or changed flow across services | The matching document in `docs/architecture/`, or a new one when none matches | With the code |
| A new or changed configuration key | The component's `.env.example` and `README.md` | With the code |
| A change to how a component is run, tested or used | The component's `README.md` | With the code |
| A task of several steps done for the second time | A guide in `docs/guides/` | With the second run |
| A failure nobody predicted | An incident in `docs/incidents/` | Before the fix |
| The end of a week | A weekly report in `docs/weekly/` | On the Friday |

A choice that affects none of the qualities in the second row needs no ADR.

## When not to write

| ID | Rule | Check |
|---|---|---|
| DOC-17 | A document MUST NOT restate code, a contract or another document; it MUST link to it instead. | Review |
| DOC-18 | A document MUST NOT describe what is not built: no plans, placeholder sections or "coming soon" notes. | Review |
| DOC-19 | A document MUST NOT list open work, because such a list goes stale; open work is tracked as an issue. | Review |
| DOC-20 | Rules MUST live only in documents of this repository. Tool and agent configuration MAY point to them, but MUST NOT define or override a rule. | Review |

## Structure of a document

Every document follows this skeleton:

```markdown
# <Subject, as a noun phrase>

<At most two sentences: what the document covers and who it is for. A binding document adds its rule prefix.>

## <The first thing the reader needs>

<Content>
```

| ID | Rule | Check |
|---|---|---|
| DOC-21 | A document MUST have exactly one level-1 heading (H1), its title, naming the subject as a noun phrase. | Review |
| DOC-22 | The paragraph under the title MUST say in at most two sentences what the document covers and who it is for. | Review |
| DOC-23 | Sections MUST be H2 headings, ordered as the reader needs them. H3 MAY split a long section; H4 and deeper MUST NOT be used. | Review |
| DOC-24 | Headings MUST use sentence case without final punctuation. | Review |
| DOC-25 | A document MUST NOT contain a hand-written table of contents or horizontal rules, because GitHub renders an outline and headings already separate sections. | Review |

## Writing

| ID | Rule | Check |
|---|---|---|
| DOC-26 | A document MUST be in English with British spelling, except a weekly report, which MUST be in Vietnamese because it goes to the supervisor. | Review |
| DOC-27 | A description MUST use the present tense and the active voice, and a step MUST use the imperative. | Review |
| DOC-28 | A sentence MUST NOT exceed 30 words, and a paragraph MUST NOT exceed four sentences. | Review |
| DOC-29 | A sentence that can be removed without losing information MUST be removed. | Review |
| DOC-30 | The words `simply`, `just`, `easily`, `obviously`, `clearly`, `very`, `robust`, `seamless` and `powerful` MUST NOT be used, because they claim a quality instead of showing it. | Review |
| DOC-31 | A domain term MUST be the one defined in `docs/core/product.md`, and one thing MUST keep one name across all documents. | Review |
| DOC-32 | An abbreviation MUST be spelled out on first use, except API, HTTP, ID, JSON, JWT, RFC, SQL, URL, UTC and UUID. | Review |
| DOC-33 | A quantity MUST be written in digits, with commas between thousands and a space before its unit, as in `20,000 students`, `300 ms` and `15 %`. Counts below ten in running text MAY be words. | Review |
| DOC-34 | A date MUST be written as `YYYY-MM-DD`, and a time in UTC as `HH:MM UTC`. | Review |
| DOC-35 | A measured number MUST state how it was measured: the command or scenario, the data size and the machine. | Review |
| DOC-36 | A statement that was not verified MUST start with `Assumption:`, and a fact from outside the repository MUST link its source. | Review |

## Formatting

| ID | Rule | Check |
|---|---|---|
| DOC-37 | Paths, file names, commands, identifiers, configuration keys, environment variables, HTTP methods with their paths, and error codes MUST be inline code. | Review |
| DOC-38 | A code block MUST declare its language, such as `bash`, `yaml`, `json`, `sql`, `text` or `mermaid`. | Review |
| DOC-39 | A command MUST run from the repository root unless its block starts with `cd`, and MUST NOT carry a prompt sign such as `$`. Its output MUST go in a separate `text` block. | Review |
| DOC-40 | Steps MUST be a numbered list, and other lists MUST be bullets. The items of one list MUST be parallel: all full sentences ending with a period, or all fragments without one. | Review |
| DOC-41 | A table MUST be used to compare items on the same attributes, and each cell MUST hold one line. | Review |
| DOC-42 | A link to a file in the repository MUST be relative, and its text MUST name the target; "here" and "this link" MUST NOT be link text. | Review |
| DOC-43 | Bold MAY mark only the term that a list item defines. Italics, capitals for emphasis and emoji MUST NOT be used. | Review |
| DOC-44 | A diagram MUST be Mermaid in a code block, because a text diagram can be reviewed in a diff. | Review |
| DOC-45 | A paragraph MUST be one line in the file, without hard wrapping, so an edit does not reflow the lines around it. | Review |

## Names

| ID | Rule | Check |
|---|---|---|
| DOC-46 | A file name MUST be kebab-case with the `.md` extension, except the names GitHub requires: `README.md`, `CONTRIBUTING.md` and `.github/pull_request_template.md`. | Review |
| DOC-47 | The main document of a folder MUST be its `README.md`. | Review |
| DOC-48 | An ADR MUST be named `NNNN-<decision-in-the-imperative>.md`, numbered after the highest existing ADR, and a number MUST NOT be reused. | Review |
| DOC-49 | An incident MUST be named `YYYY-MM-DD-<symptom>.md`, dated on the day it was detected. | Review |
| DOC-50 | A weekly report MUST be named `YYYY-MM-DD.md`, dated on its Friday. | Review |

## Length

| ID | Rule | Check |
|---|---|---|
| DOC-51 | A document MUST stay within the word limit of its kind below. A longer document MUST be split or cut. | `wc -w <file>` |

| Kind | Words at most |
|---|---|
| Core document, guide | 600 |
| Component README, architecture document | 800 |
| ADR | 1,000 |
| Standard, this one included | 2,500 |
| Incident, weekly report | No limit, because evidence is pasted verbatim |

## Keeping documents true

| ID | Rule | Check |
|---|---|---|
| DOC-52 | A change MUST update every document it makes wrong, in the same pull request. | Review |
| DOC-53 | Removing a thing MUST remove its documents and every link to it in the same pull request. | Review |
| DOC-54 | Every command in a document MUST be run, and every link MUST resolve, before the document is merged. | Review |
| DOC-55 | A document MUST NOT contain `TODO`, `FIXME`, placeholder sections or change logs, because history is in git. | Review |
| DOC-56 | A non-record document that breaks this standard MUST be brought in line by the next pull request changing it, unless that change only fixes links or names. | Review |
| DOC-57 | A change to core MUST follow an accepted ADR, and a change to core or to this standard MUST be approved by the project owner. | Review |

Reviewers check these rules through the documentation items of the [pull request template](../.github/pull_request_template.md).

## Reading order

1. [`core/product.md`](core/product.md), then [`core/rules.md`](core/rules.md)
2. The standards for the part being changed
3. The README of the component being changed, and the ADRs it links to
