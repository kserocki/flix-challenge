# ADR-0008: GitHub Actions CI and GitHub Issues as the tracker

- Status: Accepted · 2026-11-16

## Decision
- Private repo on GitHub. Work is tracked as **GitHub Issues**, one branch and one PR per issue, with small one-sentence commits.
- `.github/workflows/ci.yml`:
  - `android` job (ubuntu): `./gradlew check :app:assembleDebug`.
  - `ios` job (macos): `./gradlew iosSimulatorArm64Test compileKotlinIosArm64` (runs every KMP module's common tests on the simulator, not just `core:data`/`core:domain`, and compiles the device target too).
  - A `ci-passed` job that needs both, meant to be the single required check on `main`. Branch protection isn't available on this private repository's plan (the branch-protection API returns HTTP 403), so it's enforced by process — the PO confirms green CI before each merge — not by GitHub.
- Domain knowledge and decisions live in the repo (`docs/domain`, `docs/adr`), not in an external tool, so the repo is self-contained for reviewers.

## Consequences
- + Reviewers see the history, the decisions and the green checks in one place.
- − macOS runner minutes are expensive. The iOS job only compiles and runs common tests.
- − `macos-26` runners can be scarce; making the iOS job resilient is tracked in #17.
