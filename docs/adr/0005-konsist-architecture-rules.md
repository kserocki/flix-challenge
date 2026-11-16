# ADR-0005: Konsist for basic architecture rules

- Status: Accepted · 2026-11-16

## Decision
A `konsist-tests` JVM module runs as part of `./gradlew check`. It keeps a **small** set of rules (AGENTS.md §3, rules 1–5):
1. `core:domain` imports nothing from Ktor, Compose, Android, AndroidX or Koin.
2. No `feature:*:impl` imports another feature's `impl` package.
3. No `feature:*:impl` imports `core:data`.
4. Classes ending in `Dto` live only in `core:data`.
5. ViewModels end with `ViewModel`, sit in a `presentation` package and have `@KoinViewModel`. Use cases end with `UseCase` and have exactly one public `operator fun invoke`.

Rules are deliberately few and readable. We add a rule only when a review catches the same mistake twice.

## Consequences
- + Architecture drift fails CI.
- − Rules are source-based (imports, names), so they're heuristics, not a type system.
