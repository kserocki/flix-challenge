# ADR-0003: Koin with annotations and the Koin compiler plugin

- Status: Accepted · 2026-11-16 · amended 2026-11-16

## Context
We need DI that works in KMP modules and Android, is light, and is checked at compile time. The user asked for Koin with annotations (`@KoinViewModel`).

The original decision let `core:domain` depend on `koin-annotations` so use cases could carry `@Factory`. Issue #2 (domain layer) revisited this: AGENTS.md §3 rule 1 says `core:domain` must stay pure Kotlin, and "annotations carry no behaviour" turned out not to hold in practice (see the spike below), so the trade-off was re-spiked and the decision amended to **option D**.

### Spike evidence (Koin 4.2.2, compiler plugin 1.2.3, Kotlin 2.4.21)
| Variant | Result |
|---|---|
| `core:domain` with only `koin-annotations` (no plugin applied there), `@Factory` use case and `@Module @ComponentScan` in the domain, registered in `app` | `:app` compiles, with only the warning `KOIN-W008 Graph not verified … definitions of DomainModule are not visible on this classpath (missing hints)`. At **runtime**: `NoDefinitionFoundException`. |
| Same domain, but the `@ComponentScan("com.kserocki.flix.core.domain")` module is moved to `app` instead | `:app` fails to compile with `KOIN-D001 Missing dependency`. Scanning from `app` doesn't reach a module the compiler plugin didn't compile. |
| The Koin compiler plugin applied to `core:domain` itself | Works, but puts the Koin runtime and plugin in the domain module — rejected. |
| **D (chosen):** Koin-free domain; `app`'s `DomainModule` has one explicit `@Factory` provider function per use case | Compiles, and the graph is still checked: binding `Clock` makes the use case resolve at runtime; leaving it unbound fails at **compile time** with `KOIN-D001`. `core:domain` still compiles for iOS with zero Koin dependency. |

The Koin compiler plugin's own docs confirm this: *"every Gradle module that declares Koin definitions must apply the plugin."* A module that only has `koin-annotations` on its classpath without the plugin gets annotations the plugin never sees, so the graph is unverified there and the binding only fails at runtime — the opposite of what ADR-0003 was for.

## Decision
- Koin with `koin-annotations` and the **Koin compiler plugin** (`io.insert-koin.compiler.plugin`), not KSP. The plugin generates the module definitions and verifies the graph at compile time.
- One `@Module @ComponentScan("<root package>")` class per module with bindings. Classes are annotated `@Single`, `@Factory` or `@KoinViewModel`. Provider functions only for third-party types (`HttpClient`, `Json`, `Clock`) **and**, as of this amendment, for `core:domain` use cases (see below).
- **Option D: `core:domain` is Koin-free.** No `koin-annotations`, no compiler plugin, no `@Factory`. Use cases are plain classes with a constructor. `app`'s `DomainModule` (an `@Module`, no `@ComponentScan`) has one explicit `@Factory` provider function per use case, for example `fun getStationTimetableUseCase(repository: DepartureRepository, clock: Clock): GetStationTimetableUseCase`. This is a documented exception to "provider functions only for third-party types": the domain's purity matters more than avoiding one line of repetition per use case.
- Koin is started only in `app` (`FlixApplication`).

## Multi-module verification (plan #3)
`core:data` applies `flix.koin` and declares its own `@Module @ComponentScan` (`DataModule`), including a binding (`HttpClient`) that needs a type (`DataLogger`) bound only in `app`. `app` is where the full graph is actually checked: it applies the plugin too, registers every module (`AppModule`, `DomainModule`, `DataModule`) on `FlixKoinApplication`, and compiles with `allWarningsAsErrors = true`, so `KOIN-W008` ("graph not verified … missing hints") fails the build instead of only warning. `koinCompiler { versionCheckSeverity = "info" }` demotes the plugin's own "Kotlin version newer than verified" note, which isn't a defect. Verified by removing `core:data`'s plugin application (keeping the raw `koin-core`/`koin-annotations` dependencies) and rebuilding `app`: it fails with exactly `KOIN-W008`; reverting fixes it (impl-log #3). The platform HTTP engine (OkHttp/Darwin) is a plain Kotlin `expect`/`actual` function, not a per-platform Koin module, since picking an engine is not a DI concern.

## Alternatives
- Hilt: Android-only, can't be used in KMP `commonMain`.
- Koin DSL only: no compile-time safety.
- Manual DI: fine for one screen, but it doesn't scale and isn't what was asked.
- Applying the Koin compiler plugin to `core:domain` so use cases can carry `@Factory` directly (the original decision): works, but it puts Koin's runtime and plugin inside the domain module, which AGENTS.md §3 rule 1 and ADR-0005 rule 1 forbid.

## Consequences
- + `core:domain` is pure Kotlin with zero Koin dependency, compiles identically for Android and iOS, and Konsist forbids every `org.koin` import there (ADR-0005 rule 1).
- + The graph is still checked at compile time in `app`: a missing `Clock` or `DepartureRepository` binding fails the build with `KOIN-D001`, not the app at runtime.
- − Each domain use case's constructor is repeated once, in its `app`'s `DomainModule` provider function.
