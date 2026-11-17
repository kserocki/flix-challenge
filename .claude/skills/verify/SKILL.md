---
name: verify
description: Flix Timetable quality gates to run before every commit and PR - detekt (with Compose rules), Konsist architecture rules, KMP and Android unit tests, debug build and iOS compilation. Use before committing, before handing work to qa/reviewer, and to reproduce CI failures.
---

# Verify

Run from the repo root (or the worktree root), always with a Bash `timeout` (at least 600000 ms for full runs).

## While working (narrowest first)
| Touched | Run |
|---|---|
| `core:domain` / `core:common` / `core:testing` | `./gradlew :core:domain:testAndroidHostTest` (same for `:core:common`, `:core:testing`). `:<module>:allTests` adds the iOS simulator tests (macOS only) |
| `core:data` | `./gradlew :core:data:testAndroidHostTest` |
| `feature:*:impl` | `./gradlew :feature:timetable:impl:testDebugUnitTest` |
| `app` (DI wiring) | `./gradlew :app:testDebugUnitTest` (`KoinGraphTest`) |
| Any Kotlin | `./gradlew detekt` |
| Module structure, names, imports | `./gradlew :konsist-tests:test` |

## Before a commit that ends a work package, and before qa/review
```bash
./gradlew check :app:assembleDebug
./gradlew iosSimulatorArm64Test compileKotlinIosArm64   # macOS only (ADR-0008)
```

## UI changes (before review)
With the emulator running and the debug app installed, replay every trail in `trails/` (`trailblaze run trails/<scenario>`). A red trail is either a regression (fix the code) or an intended UI change (`qa` re-records it). Never edit the recorded actions by hand to make a trail pass. Two trails need device setup first (`.claude/agents/qa.md` "Device settings"): `timetable-offline-error` (offline) and `timetable-foreign-device-zone` (device zone `America/New_York`). Restore the device afterwards.

## Rules
- Green means green: **no baselines, no `@Suppress`** without a one-line reason next to it that the reviewer accepts, and no `@Ignore`d tests.
- A failure you don't understand → ask `consultant`. Don't change the rule config to get green.
- If a gate is slow or flaky, report it in `impl-log.md`. Don't skip it.
- Keep `AGENTS.md` §5 (the command table) in sync when a command changes.
