# ADR-0001: Multi-module with feature api/impl and core modules

- Status: Accepted · 2026-11-16

## Context
The app has one screen today, but the brief asks for production-ready, maintainable code. Module boundaries make layering a compile-time fact instead of a convention, and they show how the codebase scales.

## Decision
- `app` (composition root), `feature:<name>:api` (public contract, NavKeys only), `feature:<name>:impl` (screen, ViewModel, feature DI), `core:*` (domain, data, common, designsystem, testing).
- `feature:*:impl` depends on `feature:*:api` of others, never on another `impl`, and never on `core:data`.
- Build configuration lives in convention plugins in `build-logic` (`flix.*`), so module build files stay a few lines long.

## Consequences
- + Layer violations fail the build (missing dependency) or `./gradlew check` (Konsist).
- + Parallel, cacheable builds. Feature work stays isolated.
- − More Gradle files than a single-module app needs for one screen. It's accepted, because demonstrating the structure is part of the goal.
