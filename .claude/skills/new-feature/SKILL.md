---
name: new-feature
description: Playbook for adding a screen or a new feature module (feature:<name>:api + impl) to Flix Timetable - module setup with convention plugins, NavKey, MVI-lite ViewModel/UiState/Intent, Screen/Content with previews, Koin @Module/@KoinViewModel, Nav3 entry registration and tests. Use when adding a feature module or a new screen.
---

# New feature module / screen

## 1. Modules
- `feature/<name>/api/build.gradle.kts`: `plugins { id("flix.android.library") }` + `kotlinx-serialization` + `navigation3-runtime`.
- `feature/<name>/impl/build.gradle.kts`: `plugins { id("flix.android.feature") }`. The convention adds Compose, Koin, lifecycle, designsystem, `core:domain`, `core:common` and test deps. Add `implementation(projects.feature.<name>.api)`.
- Add both to `settings.gradle.kts`, and `impl` to `app/build.gradle.kts`.

## 2. api
```kotlin
@Serializable
data object <Name>NavKey : NavKey
```
Only NavKeys go here (Konsist).

## 3. impl, package by layer
```
com.kserocki.flix.feature.<name>.impl
├── di/<Name>Module.kt              @Module @ComponentScan("com.kserocki.flix.feature.<name>.impl")
├── navigation/<Name>Entry.kt       fun EntryProviderScope<NavKey>.<name>Entry()
└── presentation/
    ├── <Name>UiState.kt            data class, ImmutableList, defaults = initial state
    ├── <Name>Intent.kt             sealed interface
    ├── <Name>Effect.kt             sealed interface (only if needed)
    ├── <Name>ViewModel.kt          @KoinViewModel, state: StateFlow, onIntent()
    ├── <Name>Screen.kt             <Name>Screen (stateful) + <Name>Content (stateless) + previews
    └── components/                 rows, state views (plain params + lambdas)
```

## 4. ViewModel template
```kotlin
@KoinViewModel
class <Name>ViewModel(
    private val getSomething: GetSomethingUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(<Name>UiState())
    val state: StateFlow<<Name>UiState> = _state.asStateFlow()

    init { load() }

    fun onIntent(intent: <Name>Intent) {
        when (intent) {
            <Name>Intent.Retry -> load()
        }
    }
}
```
Rules: `state` changes only through `_state.update { }`. Launch in `viewModelScope`. Never expose `MutableStateFlow`. Don't do work in `init` that tests can't control (inject the `Clock` and the dispatchers).

## 5. Wiring
- Register `<Name>Module` in `app`'s `startKoin { modules(...) }`.
- Add `<name>Entry()` to the `entryProvider` in `app`.

## 6. Tests (definition of done)
- ViewModel: every intent and every state transition, with Turbine, a fake repository from `core:testing` and a test clock.
- New domain rules: `commonTest` in `core:domain`.
- Previews: one per state, light and dark.
- `verify` is green.
