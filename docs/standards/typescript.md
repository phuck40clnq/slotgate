# TypeScript standard

How TypeScript is written in `sdk/` and `examples/`, for everyone who writes or reviews it. Rules use the prefix `TS`, and they add to the [coding](coding.md) and [testing](testing.md) standards without repeating them.

## Scope

[ADR-0010](../adr/0010-build-the-sdk-and-the-demo-school-in-typescript.md) decides the folders, the tools and the dependencies. The software development kit (SDK) is part of Slot Gate. The demo school is a client of Slot Gate, so the [service](services.md) and [API](api.md) standards do not bind its server.

| Folder | Contains |
|---|---|
| `sdk/src/` | `index.ts`, the only public entry, the modules behind it, and `generated/` |
| `examples/truong-demo/web/src/` | `app/` for the router, the query client and the providers, and one folder per page in `pages/` |
| `examples/truong-demo/server/src/` | `main.ts`, the configuration, and one module per group of routes |
| `platform/typescript/` | `tsconfig.base.json`, which every package extends |

## Compiler and syntax

| ID | Rule | Check |
|---|---|---|
| TS-01 | Every package MUST extend `platform/typescript/tsconfig.base.json`, which sets `strict`, `noUncheckedIndexedAccess`, `erasableSyntaxOnly` and `verbatimModuleSyntax`, and MUST NOT turn them off. | `tsc` |
| TS-02 | Code MUST use only syntax that type stripping erases: no `enum`, no `namespace` with runtime code, no parameter properties and no decorators, because Node runs the server sources unchanged. | `tsc` |
| TS-03 | `any`, the non-null assertion `!` and `@ts-ignore` MUST NOT be used; `unknown` with narrowing replaces them. | Biome |
| TS-04 | `@ts-expect-error` MAY appear only in a test, with its reason on the same line. | Review |
| TS-05 | Every package MUST be JavaScript modules, with `"type": "module"` in its `package.json`. | Review |
| TS-06 | Exports MUST be named; a default export MAY appear only where a tool requires one, such as `vite.config.ts`. | Biome |
| TS-07 | A relative import MUST name its file with the extension, as in `./sections.ts`, because Node resolves no other form. | Biome |
| TS-08 | An import used only as a type MUST be written `import type`. | `tsc` |

## Types

| ID | Rule | Check |
|---|---|---|
| TS-09 | A closed set of outcomes MUST be a discriminated union, and its `switch` MUST assign the value to `never` in `default`, so a new case fails compilation. | `tsc` |
| TS-10 | A value that can be absent MUST be typed `T \| undefined`; `null` MAY appear only where a contract or a library returns it. | Review |
| TS-11 | A type for data that crosses a module boundary MUST be `readonly`, as CODE-14 requires immutable state. | Review |
| TS-12 | A time MUST cross a boundary as an RFC 3339 string in UTC, as the contracts write it, and become a `Date` only to compute or to display. | Review |
| TS-13 | Code that reads the current time or creates an identifier MUST receive a clock or a generator as a parameter, so that tests control both. | Review |
| TS-14 | A time MUST be displayed with `Intl.DateTimeFormat` in the time zone of the school, never with string formatting by hand. | Review |

## Contract types and the SDK

| ID | Rule | Check |
|---|---|---|
| TS-15 | Request and response types MUST come from `components["schemas"]` of the files that `openapi-typescript` generates from `contracts/openapi/`, never from their `paths`, because those are the paths behind the gateway. | Review |
| TS-16 | Generated files MUST live in `sdk/src/generated/`, MUST NOT be edited by hand, and MUST be regenerated in the change that changes their contract. | Review |
| TS-17 | The SDK MUST NOT have a runtime dependency, and MUST reach Slot Gate only through `fetch`. | Review |
| TS-18 | The SDK MUST send the ticket as `Authorization: Bearer <ticket>`, and MUST NOT send `X-Internal-User-Id`, because only the gateway sets it. | Test |
| TS-19 | An SDK call MUST resolve with its typed response, or reject with an SDK error that carries the HTTP status and the stable `code` of the error response. | Test |
| TS-20 | A network failure and an unreadable response MUST reject with SDK errors of their own codes, distinct from the codes of Slot Gate. | Test |
| TS-21 | Every export of the SDK MUST come from `sdk/src/index.ts`, with a documentation comment that has a one-sentence summary and one `@throws` line per error code. | Review |
| TS-22 | The demo school MUST import the SDK by its package name only, never by a path into `sdk/`, because a school can do only that. | Review |

## Pages

| ID | Rule | Check |
|---|---|---|
| TS-23 | Components MUST be functions, and hooks MUST follow the rules of React hooks. | Biome, review |
| TS-24 | Data from Slot Gate or from the demo school's server MUST be read and changed through TanStack Query, never copied into component state or a global store. | Review |
| TS-25 | A component MUST receive the SDK client from a React context, never import a configured instance, so that a test can provide a fake. | Review |
| TS-26 | An interactive element MUST be a native element, such as `button`, `a` or `input`, with an accessible name, because tests and assistive technology find it by role. | Test |
| TS-27 | Text shown to students MAY be Vietnamese; identifiers, comments and log messages MUST be English. | Review |
| TS-28 | A variable prefixed `VITE_` MUST NOT hold a secret, because Vite writes it into the code sent to the browser. | Review |

## Server

| ID | Rule | Check |
|---|---|---|
| TS-29 | Every route MUST declare a JSON Schema for its body, query, parameters and headers, so that invalid input is rejected at the boundary, as CODE-18 requires. | Review |
| TS-30 | Logs MUST go through the Fastify logger as JSON lines on standard output, and `console` MUST NOT be used. | Biome |
| TS-31 | Configuration MUST be read from environment variables listed in `.env.example` and checked once at start-up, and the server MUST stop when a value is missing or invalid. | Review |
| TS-32 | The API key and every other secret MUST stay on the server, and MUST NOT appear in a response, a log line or the pages. | Review |

## Dependencies

| ID | Rule | Check |
|---|---|---|
| TS-33 | Every version MUST live in the catalog of the root `pnpm-workspace.yaml`, and a `package.json` MUST refer to it with `catalog:`. | Review |
| TS-34 | A package needed at run time MUST be in `dependencies`, and a tool only in `devDependencies`. | Review |
| TS-35 | `pnpm-lock.yaml` MUST be committed, and continuous integration MUST install with `--frozen-lockfile`. | Review |
| TS-36 | Build scripts of dependencies MUST stay blocked, except for a package allowed by name in `allowBuilds`, with its reason in a comment on the same line. | Review |
| TS-37 | `minimumReleaseAge` MUST NOT be set below its default of one day, because a newer release has had less time to be checked by others. | Review |
| TS-45 | The root `package.json` MUST pin pnpm 11 in `packageManager` and require Node 24.12 or later in `engines.node`, because TS-02, TS-36 and TS-37 rely on them. | Review |

## Tests

| ID | Rule | Check |
|---|---|---|
| TS-38 | Tests MUST use Vitest, sit beside their module as `<module>.test.ts` or `<module>.test.tsx`, and use `it.each` for a table of cases. | Vitest |
| TS-39 | A page test MUST render with Testing Library in `jsdom`, find elements by role or label, and replace the SDK with a hand-written fake. | Review |
| TS-40 | An SDK test MUST call `fetch` against a local `node:http` server that the test starts and that answers in the shape of the contract, because TEST-23 forbids mocking `fetch`. | Review |
| TS-41 | A server test MUST call its routes with `inject`, without opening a port. | Review |
| TS-42 | The coverage floor of TEST-28 MUST apply to `sdk/src/`, outside `generated/`, through the coverage of Vitest. | Vitest |

## Formatting

| ID | Rule | Check |
|---|---|---|
| TS-43 | Biome MUST format every file, with 2 spaces of indentation, single quotes, semicolons and lines of at most 120 characters. | Biome |
| TS-44 | File and folder names MUST be kebab-case, as in `section-list.tsx` exporting `SectionList`. | Review |
