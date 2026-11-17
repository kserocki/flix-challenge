# Architecture

## 1. Overview

Clean Architecture in three layers, with dependencies pointing inwards to the domain:

```
              Android only                         Kotlin Multiplatform (android, iosArm64, iosSimulatorArm64)
 ┌───────────────────────────────────┐   ┌──────────────────────────────────────────────────────────────┐
 │ app  ──► feature:timetable:impl ──┼──►│ core:domain  ◄── core:data ──► Ktor, kotlinx-serialization   │
 │  │            │                   │   │      ▲               │                                       │
 │  │            ▼                   │   │      └─ core:common ◄┘                                       │
 │  └──► feature:timetable:api       │   └──────────────────────────────────────────────────────────────┘
 │       core:designsystem           │
 └───────────────────────────────────┘
```

- **Presentation** (`feature:*:impl`, Android): Compose UI and ViewModels in MVI-lite.
- **Domain** (`core:domain`, KMP): models, repository interfaces, use cases. No framework imports.
- **Data** (`core:data`, KMP): Ktor HTTP client, DTOs, mappers and repository implementations.
- `app` is the composition root. It is the only module that sees `core:data` and all feature `impl`s at once.

## 2. Modules

| Module | Type | Depends on | Contains |
|---|---|---|---|
| `app` | Android application | everything below | `FlixApplication` (starts Koin with `FlixKoinApplication`), `MainActivity`, `FlixNavHost` (Nav3 `NavDisplay`, entry registration), `AppModule` (the `Clock` binding), `DomainModule` (use-case providers), `LogcatDataLogger` |
| `feature:timetable:api` | Android library | – | `TimetableNavKey` |
| `feature:timetable:impl` | Android library | `feature:timetable:api`, `core:domain`, `core:common`, `core:designsystem` | `TimetableViewModel`, `TimetableUiState/Intent/Effect`, UI mappers (domain → UI model), `TimetableModule` (Koin), `TimetableScreen/Content`, `timetableEntry()` (Nav3 entry) |
| `core:domain` | KMP | `core:common` | `Station`, `Departure`, `StationTimetable`, `TimetableWindow`, `DepartureRepository`, `GetStationTimetableUseCase`, `DomainError` |
| `core:data` | KMP | `core:domain`, `core:common` | `FlixApiConfig`, `FlixApiClient`, `DeparturesResponseDto`, `toDomain()` mappers, `NetworkDepartureRepository`, `DataLogger` (bound by `app`), Ktor engine per platform (OkHttp / Darwin) |
| `core:common` | KMP | – | `Result<T, E>`, `suspendRunCatching` |
| `core:designsystem` | Android library | – | `FlixTheme`, colour/spacing/type tokens, `FlixTopAppBar`, shared loading and message-state components |
| `core:testing` | KMP | `core:domain`, `core:common` | `FakeDepartureRepository`, `TestClock`, fixture builders |
| `konsist-tests` | JVM | – | Architecture rules over all sources |
| `build-logic` | included build | – | Convention plugins: `flix.kmp.library`, `flix.android.library`, `flix.android.feature`, `flix.android.application`, `flix.compose`, `flix.koin`, `flix.quality` |

Only the domain and data layers are shared through KMP, by design (ADR-0002). Presentation stays Android-native, which keeps the UI idiomatic and the multiplatform surface small.

## 3. Data flow (UDF)

```
User ──Intent──► TimetableViewModel.onIntent()
                        │
                        ▼
              GetStationTimetableUseCase()
                        │  reads clock.now(), computes window [now, now + 2h]
                        ▼
              DepartureRepository.getTimetable(window)
                        │
                        ▼
              FlixApiClient (Ktor) ──► DTO ──toDomain()──► StationTimetable
                        │
                        ▼
              Result<StationTimetable, DomainError>
                        │  the use case dedups by id (TT-6) and stable-sorts by scheduledAt (TT-5)
                        ▼
      ViewModel reduces it into TimetableUiState (StateFlow) ──► TimetableContent renders
```

- The use case takes no parameters: it reads the clock itself on every call, and the station id is data-layer config (TT-3), not a use-case argument.
- State is the single source of truth for the UI. Composables never call use cases.
- The ViewModel maps domain models to **UI models** (formatted time in the station zone, day headers). Formatting is presentation logic, and it is unit tested.

## 4. Error strategy

- Ktor exceptions are caught **only** in `core:data` (with `suspendRunCatching`, which rethrows `CancellationException`) and mapped to `DomainError`, in this order:
  - `DomainError.Server(code)`: any non-2xx HTTP status (4xx/5xx), from Ktor's `ResponseException`.
  - `DomainError.Network`: no connectivity, timeout, any `kotlinx.io.IOException`.
  - `DomainError.InvalidData`: malformed JSON, a non-JSON body, an unknown or missing time zone, a missing mandatory field, and any other exception we didn't anticipate (a fallback, never a crash).
- A response where `rides` isn't empty but every ride was skipped (TT-16) also counts as `DomainError.InvalidData` (TT-16a): an empty board would misleadingly imply there is nothing to show.
- Use cases and repositories return `Result<T, DomainError>`. No exceptions cross a layer boundary.
- The UI maps each `DomainError` to a user-facing message with a retry action.

## 5. Time handling

- Points in time are `kotlin.time.Instant`. Zones are `kotlinx.datetime.TimeZone`.
- "Now" comes from an injected `kotlin.time.Clock`, so the window computation and "already departed" logic are deterministic in tests.
- Request window: `from = now`, `to = now + 2h`, both ISO-8601 UTC with milliseconds.
- Display: `departure.scheduledAt.toLocalDateTime(station.timeZone)`. **The device time zone is never used** for departure times. See `docs/domain/TIMETABLE_RULES.md`.

## 6. Dependency injection

Koin with annotations and the Koin compiler plugin (ADR-0003). This gives compile-time checks of the graph and no hand-written module DSL.
- One `@Module @ComponentScan` per module that owns classes with bindings (`DataModule`, `TimetableModule`, `AppModule`).
- `core:domain` has no Koin dependency at all; `app`'s `DomainModule` (plain `@Module`, no `@ComponentScan`) provides each use case through an explicit `@Factory` function (ADR-0003, option D).
- `core:data`'s `DataModule` declares a binding (`HttpClient`) that needs `DataLogger`, a type only `app` binds (`LogcatDataLogger`). `app` is where the whole graph, across every module, is actually verified at compile time (`KOIN-W008`/`KOIN-D001`), with `allWarningsAsErrors` making an unverified graph fail the build (ADR-0003, ADR-0009).
- `app` starts Koin with every module.

## 7. Navigation

Navigation 3 (ADR-0006). `feature:*:api` exposes a `@Serializable` `NavKey`. `feature:*:impl` contributes its entry to the `entryProvider` through an extension function. `app` owns the back stack. There is one screen today, and the structure is ready for a second one (for example a ride detail).

## 8. Quality gates

- **Detekt** with the Compose rules (`io.nlopez.compose.rules`), basic config (ADR-0007).
- **Konsist**: the rules from `AGENTS.md` §3 (ADR-0005).
- **Android Lint** (AGP defaults) on the Android modules, part of `check`.
- **Unit tests**: `commonTest` for KMP modules, JVM tests for Android modules.
- **Compose semantics tests** for `feature:*:impl` run on the JVM with Robolectric + `ui-test-junit4` (set up by `flix.android.feature`). Robolectric is pinned to `sdk=35` in each module's `src/test/resources/robolectric.properties`, because SDK 36+ sandboxes need Java 21 and CI/local use JDK 17. Raise it together with the JDK (#13).
- **UI trails**: Trailblaze trails in `trails/`, replayed on an emulator before review (ADR-0011). Not in CI yet (#6).
- **CI**: GitHub Actions (ADR-0008): `check :app:assembleDebug` on Linux, iOS simulator tests and the device compile on macOS.

## 9. Versions

The source of truth is `gradle/libs.versions.toml`. We use the latest stable versions as of the scaffold date and update them deliberately. Alphas are used only when a stable one doesn't exist and an ADR records why.
