# ADR-0002: Kotlin Multiplatform for the domain and data layers

- Status: Accepted · 2026-11-16

## Context
The user wants the domain and data layers to be Kotlin Multiplatform and consumed by the Android app. An iOS app isn't in scope.

## Decision
- `core:domain`, `core:data`, `core:common` and `core:testing` are KMP modules with targets `android` (the AGP `com.android.kotlin.multiplatform.library` target), `iosArm64` and `iosSimulatorArm64`.
- All code lives in `commonMain`. Platform code is limited to the Ktor engine (OkHttp on Android, Darwin on iOS).
- Only multiplatform libraries: Ktor, kotlinx-serialization, kotlinx-datetime, kotlinx-coroutines, `kotlin.time`.
- Presentation (ViewModels, Compose) stays Android-only in `feature:*:impl`.
- CI compiles and tests the iOS targets on macOS, so "multiplatform" is checked, not claimed.

## Alternatives
- Android + JVM targets only: simpler CI, but it doesn't prove iOS readiness.
- Compose Multiplatform for the UI too: out of scope and heavier. The brief is Android.
- A separate repository publishing `core:domain`/`core:data`/`core:common` as versioned Maven artifacts plus an XCFramework for a future iOS app: real decoupling, but it adds a publishing pipeline, version bumps and a release cadence this one-app challenge doesn't need yet.
- A separate Gradle included build for the shared modules, consumed by `app` through `includeBuild`: keeps the shared code buildable on its own, but doubles the Gradle configuration (two settings files, two version catalogs to keep in sync) for no isolation benefit over a single build with clear module boundaries.
- **Chosen: a single-build monorepo** (`settings.gradle.kts` includes every module). There is one product and one team, so atomic changes across shared code and the app land in one commit and one PR, with no publishing or versioning overhead. Konsist (ADR-0005) enforces the module boundaries that a separate build would otherwise buy.

## Consequences
- + The business logic (window, time zones, mapping, errors) is reusable by an iOS app as is.
- − iOS compilation needs a macOS CI runner.
