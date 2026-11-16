# ADR-0003: Koin with annotations and the Koin compiler plugin

- Status: Accepted · 2026-11-16

## Context
We need DI that works in KMP modules and Android, is light, and is checked at compile time. The user asked for Koin with annotations (`@KoinViewModel`).

## Decision
- Koin with `koin-annotations` and the **Koin compiler plugin** (`io.insert-koin.compiler.plugin`), not KSP. The plugin generates the module definitions and verifies the graph at compile time.
- One `@Module @ComponentScan("<root package>")` class per module with bindings. Classes are annotated `@Single`, `@Factory` or `@KoinViewModel`. Provider functions only for third-party types (`HttpClient`, `Json`, `Clock`).
- `core:domain` depends only on `koin-annotations` (annotations, no runtime), so use cases can be `@Factory`. This is a small, deliberate exception to "pure domain": annotations carry no behaviour, and the alternative (hand-written providers in `app`) duplicates every constructor.
- Koin is started only in `app` (`FlixApplication`).

## Alternatives
- Hilt: Android-only, can't be used in KMP `commonMain`.
- Koin DSL only: no compile-time safety.
- Manual DI: fine for one screen, but it doesn't scale and isn't what was asked.

## Consequences
- + A missing binding fails compilation, not the app at runtime.
- − `core:domain` has one compile-only-style dependency on annotations. Konsist allows `org.koin.core.annotation.*` and nothing else from Koin there.
