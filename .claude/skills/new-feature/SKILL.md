---
name: new-feature
description: Playbook for adding a screen or a new feature module (feature:<name>:api + impl) to Flix Timetable - module setup with convention plugins, NavKey, MVI-lite ViewModel/UiState/Intent, Screen/Content with previews, Koin @Module/@KoinViewModel, Nav3 entry registration and tests. Use when adding a feature module or a new screen.
---

# New feature module / screen

## 1. Modules
- `feature/<name>/api/build.gradle.kts`: `plugins { id("flix.android.library"); alias(libs.plugins.kotlin.serialization) }` and `dependencies { api(libs.navigation3.runtime); api(libs.kotlinx.serialization.core) }`.
- `feature/<name>/impl/build.gradle.kts`: `plugins { id("flix.android.feature") }`, nothing else. The convention adds its own `:api`, Compose, Koin, lifecycle, Navigation 3 runtime, kotlinx-collections-immutable, `core:domain`, `core:common`, `core:designsystem`, and test deps (`core:testing`, coroutines-test, Turbine, Robolectric, ui-test-junit4).
- Add both to `settings.gradle.kts`, and both `api` and `impl` to `app/build.gradle.kts`.
- If the module has a Compose semantics test (JVM, Robolectric), add `feature/<name>/impl/src/test/resources/robolectric.properties` with `sdk=35` — SDK 36+ needs Java 21, and CI/local use JDK 17 (see `docs/ARCHITECTURE.md` §8).

## 2. api
```kotlin
@Serializable
data object <Name>NavKey : NavKey
```
Only NavKeys go here (by review; Konsist doesn't check it).

## 3. impl, package by layer
```
com.kserocki.flix.feature.<name>.impl
├── di/<Name>Module.kt              @Module @ComponentScan("com.kserocki.flix.feature.<name>.impl")
├── navigation/<Name>Entry.kt       fun EntryProviderScope<NavKey>.<name>Entry()
└── presentation/
    ├── <Name>UiState.kt            data class, ImmutableList, defaults = initial state (internal)
    ├── <Name>Intent.kt             sealed interface
    ├── <Name>Effect.kt             sealed interface (only if needed)
    ├── <Name>UiMapper.kt           domain → UI model (formatting, grouping), unit tested
    ├── <Name>ViewModel.kt          @KoinViewModel, state: StateFlow, onIntent()
    ├── <Name>Screen.kt             <Name>Screen (stateful) + <Name>Content (stateless)
    ├── <Name>ContentPreviews.kt    @PreviewLightDark per state
    ├── preview/                    preview data
    └── components/                 rows, state views (plain params + lambdas)
```

## 4. ViewModel template
```kotlin
@KoinViewModel
internal class <Name>ViewModel(
    private val getSomething: GetSomethingUseCase,
) : ViewModel() {
    private val mutableState = MutableStateFlow(<Name>UiState())
    val state: StateFlow<<Name>UiState> = mutableState.asStateFlow()

    init { load() }

    fun onIntent(intent: <Name>Intent) {
        when (intent) {
            <Name>Intent.Retry -> load()
        }
    }
}
```
Rules: `state` changes only through `mutableState.update { }`. Launch in `viewModelScope`. Never expose `MutableStateFlow`. Inject `Clock` instead of reading the system time; tests drive `viewModelScope` with `Dispatchers.setMain(StandardTestDispatcher())`.

## 5. Wiring
- Add `<Name>Module::class` to `@KoinApplication(modules = [...])` in `app/src/main/kotlin/com/kserocki/flix/app/di/FlixKoinApplication.kt`. `KoinGraphTest` and the compiler plugin then check it.
- Add `<name>Entry()` to the `entryProvider { }` in `app/.../navigation/FlixNavHost.kt`.

## 6. Tests (definition of done)
- ViewModel: every intent and every state transition, with Turbine, a fake repository from `core:testing` and a test clock.
- New domain rules: `commonTest` in `core:domain`.
- UI mapper: formatting and grouping rules, with the device zone and the station zone pinned.
- Accessibility semantics (merged rows, headings, custom actions): a JVM Compose test with Robolectric (`@RunWith(RobolectricTestRunner::class)`, `createComposeRule()`).
- Previews: one per state, light and dark.
- `verify` is green.
