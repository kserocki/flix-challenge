# Flix Timetable

Android coding challenge: a live departure board for **Berlin central bus station**, showing the line code, direction and departure time in the **station's time zone** for the next two hours.

> Status: in progress. Screenshots and run instructions land with the feature PRs.

## Highlights
- **Clean Architecture, multi-module**: `app` · `feature:timetable:{api,impl}` · `core:{domain,data,common,designsystem,testing}`
- **Kotlin Multiplatform** domain and data layers (Android + iOS targets), consumed by the Android app
- **MVI-lite with unidirectional data flow**, Jetpack Compose, Navigation 3
- **Koin** with annotations and the compiler plugin (`@KoinViewModel`, compile-time graph checks)
- **Ktor + kotlinx-serialization + kotlinx-datetime**, typed errors (no exceptions across layers)
- Quality gates: **Detekt + Compose rules**, **Konsist** architecture rules, unit tests, CI on GitHub Actions

## Docs
- [Architecture](docs/ARCHITECTURE.md)
- [Decisions (ADRs)](docs/adr/README.md)
- [Domain: glossary](docs/domain/GLOSSARY.md) · [timetable rules](docs/domain/TIMETABLE_RULES.md) · [API](docs/domain/API.md) · [brief](docs/domain/CHALLENGE.md)
- [Agent guide](AGENTS.md)
