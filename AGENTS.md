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

## How to build and test

Use the Gradle wrapper — **never** invoke Gradle any other way:

```
./gradlew test          # compile and run all tests
./gradlew compileJava   # compile production code only
./gradlew build         # compile, test, and assemble
```

Gradle version: **9.6.0**. Java version: **25 EA**. Both are handled automatically by the wrapper.

Run `./gradlew test` before and after every change. All tests must pass. If any test fails, do not proceed until it is fixed.

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

Test fixtures live in `src/test/resources/fixtures/` and are taken directly from the Python repo's `tests/fixtures/` directory.

`expected_specs.json` is the ground truth for per-vehicle behaviour. Do not edit it without also updating `upstream-baseline.txt`.

## Commit every turn

After **every** turn in which you make any file change — including edits, new files, deleted files, or downloaded fixtures — create a git commit before responding. Do not batch changes across turns.

Commit message format: one concise summary line, then a blank line, then bullet points for each changed file if there is more than one. Always add the co-author trailer:

```
Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>
```

If `./gradlew test` has not been run and passed in the current turn, run it before committing.

## Package and structure

- Package root: `com.renault.api`
- Main source: `src/main/java/com/renault/api/`
- Test source: `src/test/java/com/renault/api/`
- Fixtures: `src/test/resources/fixtures/`
- Python baseline reference: `upstream-baseline.txt`
