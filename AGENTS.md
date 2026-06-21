# Agent Guidance

## What this repository is

A **direct Java port** of the Python library [`hacf-fr/renault-api`](https://github.com/hacf-fr/renault-api).
It implements the same Gigya authentication flow and Kamereon vehicle API used by Renault's MyRenault app.

The baseline Python version this port tracks is recorded in `upstream-baseline.txt`.

## Core directive: no drift from the Python implementation

This port must behave **identically** to the Python library unless the user explicitly says otherwise.

- Every public behaviour must match the Python source exactly.
- The test suite (described below) is the proof of equivalence. If a test fails, fix the code — do not relax the test.
- If the Python library is updated (check `upstream-baseline.txt` for how to diff), update both the Java code **and** the tests together so they remain in sync.
- Do not add features, remove behaviours, or change semantics relative to the Python library without explicit instruction to diverge.
- If you are unsure whether a change drifts from Python behaviour, consult the Python source before proceeding.

## Project structure

This is a multi-project Gradle build with two subprojects:

| Subproject | Purpose |
|---|---|
| `api/` | The library — published artifact, all production and test code |
| `harness/` | Integration test harness for verifying against a real vehicle |

## How to build and test

Use the Gradle wrapper — **never** invoke Gradle any other way:

```
./gradlew :api:test          # compile and run all tests
./gradlew :api:compileJava   # compile production code only
./gradlew build              # compile, test, and assemble all subprojects
```

Gradle version: **9.6.0**. Java version: **25**. Both are handled automatically by the wrapper.

Run `./gradlew test` before and after every change. This compiles **all** subprojects (including `harness/`) and runs all tests. All tasks must succeed. If any compilation error or test failure occurs, do not proceed until it is fixed.

Do not use `./gradlew :api:test` alone — it skips harness compilation and will miss build breaks there.

## Test suite overview

The tests mirror the Python test suite case-for-case. They fall into these groups:

| Class | What it tests |
|---|---|
| `GigyaModelsTest` | Gigya response model deserialization |
| `GigyaErrorTest` | Gigya error codes and exception types |
| `GigyaClientTest` | Gigya HTTP flow (MockWebServer) |
| `KamereonVehicleDataTest` | Kamereon data endpoint deserialization |
| `KamereonErrorTest` | Kamereon error codes and exception types |
| `KamereonVehicleActionTest` | Kamereon action response deserialization |
| `KamereonChargingSettingsTest` | Charge schedule parsing and mutation |
| `KamereonHvacSettingsTest` | HVAC schedule parsing and mutation |
| `KamereonClientTest` | Kamereon HTTP flow (MockWebServer) |
| `KamereonVehicleSpecsTest` | Per-model specs verified against `expected_specs.json` |

Test fixtures live in `api/src/test/resources/fixtures/` and are taken directly from the Python repo's `tests/fixtures/` directory.

`expected_specs.json` is the ground truth for per-vehicle behaviour. Do not edit it without also updating `upstream-baseline.txt`.

## Harness subproject

`harness/` is an integration test tool for verifying the library against a real vehicle. It is **never** run in CI — it requires live credentials and a real car. It has two modes:

**CLI** (interactive menu, prints raw HTTP exchange + structural fixture diff):
```
export RENAULT_USER=you@example.com RENAULT_PASS=xxx RENAULT_LOCALE=fr_FR
./gradlew :harness:run
```

**Browser UI** (side-by-side live JSON vs fixture JSON with diff table):
```
./gradlew :harness:run -PmainClass=com.renault.harness.HarnessServer
# open http://localhost:8080
```

Optional env vars:
- `RENAULT_FIXTURE_PATH` — path to `vehicle_data` fixture directory (default: `api/src/test/resources/fixtures/kamereon/vehicle_data`)

To add Renault 5-specific fixtures: run the harness against the car, copy the live JSON output into a new fixture file under `api/src/test/resources/fixtures/kamereon/vehicle_data/`, then write a `@Test` in `KamereonVehicleDataTest` referencing it.

The harness only calls **read** endpoints by default. It does not send commands to the car.

## Commit every turn

After **every** turn in which you make any file change — including edits, new files, deleted files, or downloaded fixtures — create a git commit before responding. Do not batch changes across turns.

Commit message format: one concise summary line, then a blank line, then bullet points for each changed file if there is more than one. Always add the co-author trailer:

```
Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>
```

If `./gradlew test` has not been run and passed in the current turn, run it before committing. This must succeed in full — no compilation errors in any subproject, no test failures.

## Javadoc style

Always write Javadoc using Markdown syntax (enabled by default in Java 23+). Use `///` line comments with standard Markdown — backticks for inline code, fenced blocks for multi-line examples, `**bold**`, bullet lists — rather than the legacy `/** */` block comments with `{@code ...}`, `<pre>`, `<ul>/<li>` HTML tags. `{@link}` inline tags are still valid inside `///` comments.

## Package and structure

- Package root: `com.renault.api`
- Library source: `api/src/main/java/com/renault/api/`
- Library tests: `api/src/test/java/com/renault/api/`
- Test fixtures: `api/src/test/resources/fixtures/`
- Harness source: `harness/src/main/java/com/renault/harness/`
- Python baseline reference: `upstream-baseline.txt`
