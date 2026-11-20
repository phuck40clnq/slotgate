# Testing standard

How automated tests are written and run in every service, for everyone who writes or reviews them. Rules use the prefix `TEST`, and `docs/standards/<language>.md` names the tools and conventions of each language.

## Principles

- Tests are the specification: a reader learns what a component does from its tests
- The cheapest test that can catch a defect catches it, on the developer's machine first
- The same code gives the same result on every run and every machine
- A test needs one command and no manual setup

## Test types

| Type | Covers | Runs |
|---|---|---|
| Unit | Domain and application logic, without a framework context or I/O | Every build |
| Architecture | Layering and forbidden dependencies of CORE-17 | Every build |
| Integration | One adapter against the real technology it talks to | Every build, after unit tests |
| Contract | API conformance and event schema compatibility | Continuous integration |
| End-to-end | The running system through the gateway | Continuous integration |
| Scenario | One failure scenario of the product requirements, reproduced on purpose | On demand |

## What must be tested

| ID | Rule | Check |
|---|---|---|
| TEST-01 | Every change of behaviour MUST come with a test that fails without the change. | Review |
| TEST-02 | A bug fix MUST start with a test that reproduces the bug. | Review |
| TEST-03 | Every domain rule MUST have unit tests for the cases it accepts and the cases it rejects. | Review |
| TEST-04 | Every adapter MUST have an integration test against the real technology, not a mock of it. | Review |
| TEST-05 | An operation that must stay atomic under concurrency MUST have a test that runs many parallel callers and asserts the invariant. | Review |
| TEST-06 | A service MUST NOT rely on a test that only starts its application as its single test. | Review |
| TEST-07 | Each failure scenario of the product requirements MUST have a scenario test that reproduces it and prints the before and after numbers of CORE-24. | Review |
| TEST-08 | A scenario test MUST run with one command, only on demand, and its component README MUST give that command and its duration. | Review |

## Structure

| ID | Rule | Check |
|---|---|---|
| TEST-09 | Test code MUST mirror the module of the code under test. | Review |
| TEST-10 | A test name MUST be a sentence that states the behaviour, as in `rejectsHoldWhenSectionIsFull`. | Review |
| TEST-11 | A test MUST check one behaviour; several assertions MAY describe that one behaviour. | Review |
| TEST-12 | Arrange, act and assert MUST be separated by one blank line each, without comments that name them. | Review |
| TEST-13 | A test MUST NOT contain logic such as conditions, loops or exception handling; a table of cases MUST be a parameterised test. | Review |
| TEST-14 | An assertion MUST check an observable outcome, not the internal calls that produced it. | Review |

## Determinism

| ID | Rule | Check |
|---|---|---|
| TEST-15 | Time MUST come from a fixed clock and identifiers from a deterministic generator. | Review |
| TEST-16 | A test MUST NOT sleep; it MUST await an asynchronous outcome with a bounded timeout. | Review |
| TEST-17 | Tests MUST NOT depend on their order or share mutable state, and a test that shares a database MUST use data of its own. | Review |
| TEST-18 | A randomised test MUST use a fixed seed and print it when it fails. | Review |
| TEST-19 | A unit test MUST NOT touch the network, the file system or the system clock. | Review |
| TEST-20 | A test that needs infrastructure MUST start it itself, without Docker on the developer's machine. | Review |

## Test doubles and data

| ID | Rule | Check |
|---|---|---|
| TEST-21 | A test double MAY replace only an outbound port, and only in a test of application logic. | Review |
| TEST-22 | Domain objects, value objects and records MUST NOT be mocked; tests build real ones. | Review |
| TEST-23 | Technology the project does not own MUST NOT be mocked; the test runs the real technology instead. | Review |
| TEST-24 | A hand-written fake MUST replace a mocking library whenever the fake is shorter. | Review |
| TEST-25 | Test data MUST come from small fixture factories per module, as in `SeatFixtures.fullSection()`, not from copied literals. | Review |

## Failing, flaky and disabled tests

| ID | Rule | Check |
|---|---|---|
| TEST-26 | A flaky test is a defect, and MUST be fixed or removed, never retried until it passes. | Review |
| TEST-27 | A disabled test MUST state its reason and its issue where it is disabled. | Review |

## Coverage

| ID | Rule | Check |
|---|---|---|
| TEST-28 | Line coverage of domain and application code MUST stay at or above 80 %, and branch coverage at or above 70 %; the build MUST fail below either. | Coverage check |
| TEST-29 | A test MUST assert behaviour, because coverage is a floor and executed code without an assertion proves nothing. | Review |
