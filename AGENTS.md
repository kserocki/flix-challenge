# AGENTS.md: Flix Timetable

An Android app that shows the **live departure board of one Flix station** (Berlin central bus station) for the next two hours. Built for the Flix Android coding challenge, so it is judged on **production-ready, clean, testable, maintainable code**, UI polish and edge-case handling. Quality beats speed.

The **domain and data layers are Kotlin Multiplatform** (Android + iOS targets, no iOS app). The UI is Android-only Jetpack Compose.

## 0. The one rule: never guess

**If anything is unclear, ambiguous or missing (behaviour, UX copy, a colour, an API field, which module owns something), stop and ask before writing code.**
- Don't invent screens, fields or behaviour that aren't in `docs/`.
- Don't silently "fix" a conflict between docs and code. Point it out and ask.
- When the user approves an assumption, record it (ADR or `docs/domain/`) in the same change.

## 1. Read before you work

| Need | Read |
|---|---|
| Modules, layers, data flow, error strategy | `docs/ARCHITECTURE.md` |
| Why a decision was made | `docs/adr/` (index in `docs/adr/README.md`) |
| Domain language, timetable rules, time zones | `docs/domain/GLOSSARY.md`, `docs/domain/TIMETABLE_RULES.md` |
| API endpoint, JSON → domain mapping, edge cases | `docs/domain/API.md` (+ a real response in `docs/domain/departures-response-sample.json`) |
| The original challenge brief | `docs/domain/CHALLENGE.md` |

## 2. Repository map

```
app/                          Android application: MainActivity, Koin start, Nav3 host (composition root)
feature/timetable/api         NavKey of the timetable screen (public contract)
feature/timetable/impl        Timetable screen: ViewModel (MVI-lite), Compose UI, feature DI
core/domain        (KMP)      Models, repository interfaces, use cases, DomainError. Pure Kotlin
core/data          (KMP)      Ktor client, DTOs, mappers, repository implementations
core/common        (KMP)      Result type, dispatchers, Clock provider, cancellation-safe helpers
core/designsystem             Compose theme, tokens, shared components
core/testing       (KMP)      Fakes, test dispatcher rule, fixture builders
build-logic/                  Convention plugins (flix.*)
konsist-tests/                Architecture rules (Konsist), run by ./gradlew check
config/detekt/                Detekt config incl. Compose rules
docs/                         Architecture, ADRs, domain knowledge
.claude/                      Agents, skills, git-ignored work folders
```

## 3. Architecture rules (Konsist enforces 1–5 in `./gradlew check`, 6 by review)

1. `core:domain` is pure Kotlin: no Ktor, Compose, `android.*` or `androidx.*` imports. From Koin, only `org.koin.core.annotation.*` is allowed (ADR-0003).
2. `feature:*:impl` never depends on another `feature:*:impl`. It navigates only through `feature:*:api` NavKeys.
3. `feature:*:impl` never depends on `core:data`. It sees domain interfaces and use cases only.
4. DTOs (`*Dto`) live only in `core:data`. Mapping is explicit (`toDomain()`).
5. ViewModels end with `ViewModel`, live in a `presentation` package and are annotated `@KoinViewModel`. Use cases end with `UseCase` and expose a single `operator fun invoke`.
6. `app` is the only composition root (`startKoin`, Nav3 `NavDisplay`).

## 4. Conventions

- **Screens, MVI-lite with UDF** (ADR-0004): `XUiState` (immutable data class, `ImmutableList` for lists), `sealed interface XIntent`, optional `sealed interface XEffect` for one-off events. `XViewModel` exposes `val state: StateFlow<XUiState>` and `fun onIntent(intent: XIntent)`. Stateful `XScreen` (gets the VM via `koinViewModel()`) → stateless `XContent(state, onIntent, modifier)` with a `@Preview` per state, light and dark. Only `XContent` knows `XIntent`. Child composables take plain values and lambdas.
- **Use cases:** `VerbNounUseCase`, one `operator fun invoke`, no Android types.
- **Repositories:** interface in `core:domain` (`DepartureRepository`), implementation in `core:data` (`NetworkDepartureRepository`).
- **Errors:** repositories and use cases return `Result<T, DomainError>` (our own type in `core:common`, not `kotlin.Result`). No exceptions across layer boundaries. Never swallow `CancellationException` (use `suspendRunCatching`).
- **Time:** `kotlin.time.Instant` for points in time and `kotlinx.datetime.TimeZone` for zones. Never call `Clock.System` directly outside DI: inject `Clock` so tests control "now". Departure times are shown in the **station's** time zone, never the device's (`docs/domain/TIMETABLE_RULES.md`).
- **DI (Koin annotations + compiler plugin, ADR-0003):** each module with bindings has one `@Module @ComponentScan("<module root package>")` class. Annotate classes with `@Single` (repositories, HTTP client holders), `@Factory` (use cases) and `@KoinViewModel` (ViewModels). Provider functions only for third-party types (`HttpClient`, `Json`, `Clock`).
- **Strings:** all user-visible text in `strings.xml`. No hard-coded UI strings, colours or dimensions outside `core:designsystem`.
- **Tests:** `kotlin.test` + `kotlinx-coroutines-test` + Turbine. Fakes over mocks (fakes live in `core:testing`). Test names in backticks describe behaviour: `` `shows empty state when no departures in window` ``.
- **Comments:** English, only for non-obvious logic, 1–3 lines. No ticket numbers except `TODO(#N)` pointing at an open GitHub issue.
- **Commits:** small, one sentence, imperative, no prefix, no co-author trailer. Example: `Add departure mapper with station time zone handling`.

## 5. Commands (all from the repo root)

| What | Command | Status |
|---|---|---|
| Every gate (detekt, Konsist, unit tests, lint) | `./gradlew check` | set up in #1 |
| Build debug APK + compile iOS targets | `./gradlew :app:assembleDebug :core:data:compileKotlinIosSimulatorArm64` | set up in #1 |
| Unit tests of one module | `./gradlew :core:domain:allTests` (KMP) · `./gradlew :feature:timetable:impl:testDebugUnitTest` (Android) | set up in #1 |
| Detekt only | `./gradlew detekt` | set up in #1 |
| Konsist only | `./gradlew :konsist-tests:test` | set up in #1 |
| Install + launch on emulator | `./gradlew :app:installDebug && adb shell am start -n <applicationId>/.MainActivity` | set up in #1 |
| Hit the real API | see `docs/domain/API.md` (curl with computed `from`/`to`) | works |

CI (GitHub Actions, `.github/workflows/ci.yml`) runs `./gradlew check :app:assembleDebug` on Linux and the iOS compilation on macOS. Reproduce a CI failure locally with the same command.

## 6. Skills (`.claude/skills/`)

| Skill | Use when |
|---|---|
| `implement` | implementing a GitHub issue (`#N`) with the agent team, from plan to PR |
| `new-feature` | adding a screen or a `feature:*:{api,impl}` module |
| `verify` | before every commit and PR |

## 7. Agent team (`.claude/agents/`)

The orchestrator (main session) is also the **PO**: it owns `docs/domain/`, talks to the user, routes questions and owns git/GitHub.

| Agent | Model | Role |
|---|---|---|
| `planner` | Opus | Writes the plan and work packages. Never writes production code. |
| `core-dev` | Sonnet | KMP `core:*` modules, ViewModels and use cases, build-logic, CI |
| `ui-dev` | Sonnet | `core:designsystem`, `XScreen`/`XContent`, previews, resources, `app` shell |
| `consultant` | Opus | Advises stuck implementers (KMP, Koin, Compose, Gradle). Never writes production code. |
| `qa` | Sonnet | Runs the app on the emulator, walks test scenarios, captures screenshots. Never fixes code. |
| `reviewer` | Opus | Reviews the diff against plan, docs and this file: `APPROVED` or `CHANGES_REQUESTED` |

## 8. Definition of done

- `verify` passes: `./gradlew check` and the build command are green, **no baselines, no suppressions** without a written reason.
- New behaviour has unit tests (domain + data + ViewModel).
- New UI has a preview per state (light and dark) and was checked on the emulator by `qa`.
- Docs, ADRs and `docs/domain/` are updated when behaviour or a decision changed.
- No `TODO` without a GitHub issue number.
