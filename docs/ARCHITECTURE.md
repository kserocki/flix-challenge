# Architecture

## 1. Overview

Clean Architecture in three layers, with dependencies pointing inwards to the domain:

```
              Android only                         Kotlin Multiplatform (androidTarget, iosArm64, iosSimulatorArm64)
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
| `app` | Android application | everything below | `FlixApplication` (Koin start), `MainActivity`, Nav3 `NavDisplay`, entry registration |
| `feature:timetable:api` | Android library | – | `TimetableNavKey` |
| `feature:timetable:impl` | Android library | `feature:timetable:api`, `core:domain`, `core:common`, `core:designsystem` | `TimetableViewModel`, `TimetableUiState/Intent`, `TimetableScreen/Content`, UI mappers (domain → UI model) |
| `core:domain` | KMP | `core:common` | `Station`, `Departure`, `StationTimetable`, `DepartureRepository`, `GetStationTimetableUseCase`, `DomainError` |
| `core:data` | KMP | `core:domain`, `core:common` | `FlixApiClient`, `DeparturesResponseDto`, `toDomain()` mappers, `NetworkDepartureRepository`, Ktor engine per platform (OkHttp / Darwin) |
| `core:common` | KMP | – | `Result<T, E>`, `suspendRunCatching`, `DispatcherProvider` |
| `core:designsystem` | Android library | – | `FlixTheme`, colour/spacing/type tokens, shared components (error/empty states) |
| `core:testing` | KMP | `core:domain`, `core:common` | `FakeDepartureRepository`, `TestClock`, fixture builders |
| `konsist-tests` | JVM | – | Architecture rules over all sources |
| `build-logic` | included build | – | Convention plugins: `flix.kmp.library`, `flix.android.library`, `flix.android.feature`, `flix.android.application`, `flix.compose`, `flix.koin`, `flix.quality` |

Only the domain and data layers are shared through KMP, by design (ADR-0002). Presentation stays Android-native, which keeps the UI idiomatic and the multiplatform surface small.

## 3. Data flow (UDF)

```
User ──Intent──► TimetableViewModel.onIntent()
                        │
                        ▼
              GetStationTimetableUseCase(now = clock.now())
                        │  computes window [now, now + 2h]
                        ▼
              DepartureRepository.getTimetable(stationId, window)
                        │
                        ▼
              FlixApiClient (Ktor) ──► DTO ──toDomain()──► StationTimetable
                        │
                        ▼
              Result<StationTimetable, DomainError>
                        │
                        ▼
      ViewModel reduces it into TimetableUiState (StateFlow) ──► TimetableContent renders
```

- State is the single source of truth for the UI. Composables never call use cases.
- The ViewModel maps domain models to **UI models** (formatted time in the station zone, day headers). Formatting is presentation logic, and it is unit tested.

## 4. Error strategy

- Ktor exceptions are caught **only** in `core:data` (with `suspendRunCatching`, which rethrows `CancellationException`) and mapped to `DomainError`:
  - `DomainError.Network`: no connectivity, timeout.
  - `DomainError.Server(code)`: HTTP 5xx or an unexpected status.
  - `DomainError.InvalidData`: malformed JSON, unknown time zone id, missing mandatory fields.
- Use cases and repositories return `Result<T, DomainError>`. No exceptions cross a layer boundary.
- The UI maps each `DomainError` to a user-facing message with a retry action.

## 5. Time handling

- Points in time are `kotlin.time.Instant`. Zones are `kotlinx.datetime.TimeZone`.
- "Now" comes from an injected `kotlin.time.Clock`, so the window computation and "already departed" logic are deterministic in tests.
- Request window: `from = now`, `to = now + 2h`, both ISO-8601 UTC with milliseconds.
- Display: `scheduledDeparture.toLocalDateTime(station.timeZone)`. **The device time zone is never used** for departure times. See `docs/domain/TIMETABLE_RULES.md`.

## 6. Dependency injection

Koin with annotations and the Koin compiler plugin (ADR-0003). This gives compile-time checks of the graph and no hand-written module DSL.
- One `@Module @ComponentScan` per module that has bindings (`DomainModule`, `DataModule`, `TimetableModule`, `AppModule`).
- `core:domain` uses only `koin-annotations` (annotations only, no runtime). This is an accepted trade-off, see ADR-0003.
- `app` starts Koin with every module.

## 7. Navigation

Navigation 3 (ADR-0006). `feature:*:api` exposes a `@Serializable` `NavKey`. `feature:*:impl` contributes its entry to the `entryProvider` through an extension function. `app` owns the back stack. There is one screen today, and the structure is ready for a second one (for example a ride detail).

## 8. Quality gates

- **Detekt** with the Compose rules (`io.nlopez.compose.rules`), basic config (ADR-0007).
- **Konsist**: the rules from `AGENTS.md` §3 (ADR-0005).
- **Unit tests**: `commonTest` for KMP modules, JVM tests for Android modules.
- **CI**: GitHub Actions (ADR-0008).

## 9. Versions

The source of truth is `gradle/libs.versions.toml`. We use the latest stable versions as of the scaffold date and update them deliberately. Alphas are used only when a stable one doesn't exist and an ADR records why.
