# ADR-0008: GitHub Actions CI and GitHub Issues as the tracker

- Status: Accepted · 2026-11-16

## Decision
- Private repo on GitHub. Work is tracked as **GitHub Issues**, one branch and one PR per issue, with small one-sentence commits.
- `.github/workflows/ci.yml`:
  - `android` job (ubuntu): `./gradlew check :app:assembleDebug`.
  - `ios` job (macos): `./gradlew :core:data:iosSimulatorArm64Test :core:domain:iosSimulatorArm64Test`.
  - A `ci-passed` job that needs both. It's the required check on `main`.
- Domain knowledge and decisions live in the repo (`docs/domain`, `docs/adr`), not in an external tool, so the repo is self-contained for reviewers.

## Consequences
- + Reviewers see the history, the decisions and the green checks in one place.
- − macOS runner minutes are expensive. The iOS job only compiles and runs common tests.
