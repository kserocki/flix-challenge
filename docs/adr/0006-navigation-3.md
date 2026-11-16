# ADR-0006: Navigation 3

- Status: Accepted · 2026-11-16

## Decision
Jetpack Navigation 3. `feature:*:api` exposes a `@Serializable data object/class XNavKey : NavKey`. `feature:*:impl` exposes `fun EntryProviderScope<NavKey>.xEntry()`. `app` owns a `rememberNavBackStack(TimetableNavKey)` and a `NavDisplay`, with ViewModel scoping per entry.

## Consequences
- + The back stack is plain state. Features don't know about each other.
- − With one screen, navigation is mostly structural. It's here so a second screen is a small change.
