# ADR-0004: MVI-lite presentation with unidirectional data flow

- Status: Accepted · 2026-11-16

## Decision
Per screen:
- `XUiState`: one immutable `data class`, the only thing the UI renders. Lists are `ImmutableList`, so Compose treats them as stable.
- `XIntent`: a `sealed interface` of user actions (`Retry`, `Refresh`).
- `XEffect` (optional): a `sealed interface` of one-off events (for example a snackbar). It's delivered with a `Channel` and consumed once.
- `XViewModel`: `val state: StateFlow<XUiState>`, `fun onIntent(intent: XIntent)`, and optionally `val effects: Flow<XEffect>`. State changes only inside the ViewModel through `update { }`.
- `XScreen` (stateful) collects the state with `collectAsStateWithLifecycle()`, collects effects in `LaunchedEffect(Unit)` under `repeatOnLifecycle(STARTED)`, and passes the state to `XContent(state, onIntent, modifier)` (stateless, previewable; it may also take hoisted UI holders such as a `SnackbarHostState`).
- One load at a time: an intent that arrives while a load is running is ignored.
- No reducer framework and no middleware ("lite"): a `when (intent)` in `onIntent` is enough at this size.

## Consequences
- + Every UI state is a value, so it's trivial to preview and to assert in tests.
- + One entry point for user actions makes the ViewModel easy to test.
- − Slightly more types than plain MVVM for a tiny screen.
