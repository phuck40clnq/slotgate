# ADR-0010: Build the software development kit (SDK) and the demo school in TypeScript on one pnpm workspace

## Status

Proposed

## Context

ADR-0009 proposes that schools call Slot Gate from their own pages through an SDK, shown by a fictional demo school. Both run partly in a browser, so they are written in JavaScript or a language compiled to it. On 2026-11-26 the project owner chose TypeScript, React, Vite, React Router and TanStack Query for the pages, and Node for the demo school's server.

Constraints:

- The [seat inventory contract](../../contracts/openapi/seat-inventory/seat-inventory.openapi.yaml) describes the paths behind the gateway, `/api/v1/seat-inventory/...`, and the `X-Internal-User-Id` header the gateway sets. A browser calls `/api/seat-inventory/...` with a token instead.
- A school's page runs on its own origin, and the gateway accepts cross-origin requests only from `http://localhost:3000`, fixed in `CorsConfig`.
- CODE-35 keeps dependency versions in one place per language, and every dependency is one more package to update and trust.

On the development laptop, Node 24.22 runs `node main.ts` without a flag or a warning, where `main.ts` imports another `.ts` file. Node documents [type stripping](https://nodejs.org/api/typescript.html#type-stripping) as stable since 24.12, for syntax it can erase.

## Options

| Choice | Chosen, and why | Rejected, and why |
|---|---|---|
| Package manager | pnpm: one [catalog](https://pnpm.io/catalogs) of versions; [blocks build scripts and releases under a day old](https://pnpm.io/supply-chain-security) | npm: versions per `package.json`; install scripts run by default ([`ignore-scripts`](https://docs.npmjs.com/cli/v11/using-npm/config#ignore-scripts)) |
| Demo school server | Fastify: [validates requests against JSON Schema](https://fastify.dev/docs/latest/Reference/Validation-and-Serialization/#validation), [logs JSON through pino](https://fastify.dev/docs/latest/Reference/Logging/), [tests inject requests](https://fastify.dev/docs/latest/Guides/Testing/) | Hono: a [thin validator](https://hono.dev/docs/guides/validation) and a [plain-text logger](https://hono.dev/docs/middleware/builtin/logger); `node:http` alone: routing, cookies and validation by hand |
| Server build | None: Node runs the sources by type stripping | `tsx` or `ts-node`: one more dependency; `tsc` output: one more step before each run |
| Tests | Vitest: [reads the Vite configuration and offers `jsdom`](https://vitest.dev/guide/environment) | Jest: [Babel without type checks, or the `ts-jest` preprocessor](https://jestjs.io/docs/getting-started#using-typescript), beside Vite; `node:test`: no browser environment |
| Contract types | `openapi-typescript`: [types only, no runtime code](https://openapi-ts.dev/) | A generated client: it calls the paths behind the gateway; hand-written types: drift from the contract |
| Lint and format | Biome: [one tool for both](https://biomejs.dev/), with [React hook rules](https://biomejs.dev/linter/rules/use-exhaustive-dependencies/) | ESLint with [`typescript-eslint`](https://typescript-eslint.io/getting-started/), [`eslint-plugin-react-hooks`](https://react.dev/reference/eslint-plugin-react-hooks) and Prettier: five packages; none: hook mistakes reach review |
| Pages calling Slot Gate | Directly across origins, as a school's page does | Through the Vite proxy: hides the cross-origin setup that every school needs |

## Decision

Write the SDK and the demo school in TypeScript, in one pnpm workspace at the repository root:

| Folder | Holds | Runs in |
|---|---|---|
| `sdk/` | The SDK, compiled by `tsc` to JavaScript modules with type declarations | Browsers |
| `examples/truong-demo/web/` | The demo school's pages, with React, Vite, React Router and TanStack Query | Browsers |
| `examples/truong-demo/server/` | The demo school's server, with Fastify: sign-in of seeded accounts, the API key, tickets for the SDK, webhooks | Node 24 |
| `platform/typescript/` | The shared compiler configuration | Build only |

- Dependency versions live in the catalog of the root `pnpm-workspace.yaml`. CODE-35 changes to allow it, because pnpm reads catalogs only there.
- The root `package.json` pins pnpm 11 in `packageManager` and Node 24.12 or later in `engines.node`. Earlier versions lack the build settings of pnpm 11 or stable type stripping.
- The SDK has no runtime dependency and calls Slot Gate with `fetch`. Its calls are written by hand against the gateway's paths.
- The SDK takes types from the schemas that `openapi-typescript` generates from `contracts/openapi/`, and the generated files are committed beside its code.
- The demo school imports the SDK by its package name only, as a school would.
- Biome lints and formats every package, and Vitest tests every package.
- The pages call the gateway directly, and the Vite proxy forwards only the calls to the demo school's own server.
- The gateway reads its allowed origins from an environment variable. A list per tenant, where each school registers its domains, comes with the tenant decision.

Each dependency enters with the first code that needs it:

| Package | Runtime | Development |
|---|---|---|
| Root | None | `@biomejs/biome` |
| `sdk/` | None | `typescript`, `openapi-typescript`, `vitest`, `@vitest/coverage-v8` for the floor of TEST-28, `@types/node` |
| `web/` | `react`, `react-dom`, `react-router`, `@tanstack/react-query` | `typescript`, `vite`, `@vitejs/plugin-react`, `vitest`, `jsdom`, `@testing-library/react`, `@testing-library/dom`, `@types/react`, `@types/react-dom` |
| `server/` | `fastify`, `@fastify/cookie` | `typescript`, `vitest`, `@types/node` |

[The TypeScript standard](../standards/typescript.md) holds the rules this code follows.

## Consequences

Better:

- A school copies the SDK calls the demo school makes, because the demo school uses only the public SDK.
- One install and one lockfile serve every TypeScript package, and versions cannot drift between them.
- The server runs without a build step, and tests run the same sources.

Worse:

- The repository gains a second toolchain beside Java and Maven, and continuous integration installs both.
- A contributor on pnpm 10 or on Node before 24.12 upgrades first.
- The root holds `package.json`, `pnpm-workspace.yaml` and `pnpm-lock.yaml`, files of one language among several.
- Type stripping forbids enums, namespaces with runtime code and parameter properties, so the code uses unions and plain classes instead.
- The demo school shares the workspace and versions of Slot Gate, which a real school would not, so it never tests an SDK upgrade the way a school meets it.
- Fastify brings transitive packages of its own, each one more to update and trust.
- Assumption: Biome covers fewer rules than ESLint with its plugins, so some mistakes reach review instead.
- Generated types are committed and go stale when a contract changes without regenerating them.
- Until the gateway reads origins from the environment, the pages must run on port 3000.
- A static origin list means adding a school changes configuration and restarts the gateway.
