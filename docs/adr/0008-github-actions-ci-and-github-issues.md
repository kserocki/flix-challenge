# ADR-0008: GitHub Actions CI and GitHub Issues as the tracker

- Status: Accepted · 2026-11-16 · amended 2026-11-17 (#17)

## Decision
- Private repo on GitHub. Work is tracked as **GitHub Issues**, one branch and one PR per issue, with small one-sentence commits.
- `.github/workflows/ci.yml`:
  - `android` job (ubuntu): `./gradlew check :app:assembleDebug`.
  - `ios` job on `macos-15` (arm64) with Xcode 26.3 selected: `./gradlew iosSimulatorArm64Test compileKotlinIosArm64`. It runs every KMP module's common tests on the simulator and compiles the device target. On pull requests it runs only when a `changes` job (`dorny/paths-filter`) sees KMP sources, build logic, the version catalog or this workflow change. On `main` it always runs.
  - **Amendment (2026-11-17, #17):** we moved off `macos-26`, because its runners stopped being assigned for this private repository (jobs sat queued and were cancelled after 15 min). `macos-15` has far more capacity. Kotlin 2.4.21 is built against Xcode 26.4 and only needs an iOS SDK, which Xcode 26.3 provides.
  - A `ci-passed` job that needs `changes`, `android` and `ios`. It fails unless each one succeeded; `ios` may be `skipped` only when the path filter skipped it on a pull request. It is meant to be the single required check on `main`. Branch protection isn't available on this private repository's plan (the branch-protection API returns HTTP 403), so it's enforced by process — the PO confirms green CI before each merge — not by GitHub.
- Domain knowledge and decisions live in the repo (`docs/domain`, `docs/adr`), not in an external tool, so the repo is self-contained for reviewers.

## Consequences
- + Reviewers see the history, the decisions and the green checks in one place.
- − macOS runner minutes are expensive. The iOS job only compiles and runs common tests.
- − macOS runners can still be scarce. The path filter keeps iOS runs to the changes that matter, and `ci-passed` turns red, not falsely green, if iOS never ran.
