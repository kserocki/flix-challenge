---
name: verify
description: Flix Timetable quality gates to run before every commit and PR - detekt (with Compose rules), Konsist architecture rules, KMP and Android unit tests, debug build and iOS compilation. Use before committing, before handing work to qa/reviewer, and to reproduce CI failures.
---

# Verify

Run from the repo root (or the worktree root), always with a Bash `timeout` (at least 600000 ms for full runs).

## While working (narrowest first)
| Touched | Run |
|---|---|
| `core:domain` / `core:common` | `./gradlew :core:domain:jvmTest` if a JVM target exists, otherwise `:core:domain:testAndroidHostTest` (the task name depends on the KMP Android target, see `./gradlew :core:domain:tasks --group verification`) |
| `core:data` | the same for `:core:data` |
| `feature:*:impl` | `./gradlew :feature:timetable:impl:testDebugUnitTest` |
| Any Kotlin | `./gradlew detekt` |
| Module structure, names, imports | `./gradlew :konsist-tests:test` |

## Before a commit that ends a work package, and before qa/review
```bash
./gradlew check :app:assembleDebug
./gradlew :core:domain:iosSimulatorArm64Test :core:data:iosSimulatorArm64Test   # macOS only
```

## Rules
- Green means green: **no baselines, no `@Suppress`** without a one-line reason next to it that the reviewer accepts, and no `@Ignore`d tests.
- A failure you don't understand → ask `consultant`. Don't change the rule config to get green.
- If a gate is slow or flaky, report it in `impl-log.md`. Don't skip it.
- Keep `AGENTS.md` §5 (the command table) in sync when a command changes.
