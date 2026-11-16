# ADR-0002: Kotlin Multiplatform for the domain and data layers

- Status: Accepted · 2026-11-16

## Context
The user wants the domain and data layers to be Kotlin Multiplatform and consumed by the Android app. An iOS app isn't in scope.

## Decision
- `core:domain`, `core:data`, `core:common` and `core:testing` are KMP modules with targets `androidTarget` (via `com.android.kotlin.multiplatform.library`), `iosArm64` and `iosSimulatorArm64`.
- All code lives in `commonMain`. Platform code is limited to the Ktor engine (OkHttp on Android, Darwin on iOS).
- Only multiplatform libraries: Ktor, kotlinx-serialization, kotlinx-datetime, kotlinx-coroutines, `kotlin.time`.
- Presentation (ViewModels, Compose) stays Android-only in `feature:*:impl`.
- CI compiles and tests the iOS targets on macOS, so "multiplatform" is checked, not claimed.

## Alternatives
- Android + JVM targets only: simpler CI, but it doesn't prove iOS readiness.
- Compose Multiplatform for the UI too: out of scope and heavier. The brief is Android.

## Consequences
- + The business logic (window, time zones, mapping, errors) is reusable by an iOS app as is.
- − iOS compilation needs a macOS CI runner.
