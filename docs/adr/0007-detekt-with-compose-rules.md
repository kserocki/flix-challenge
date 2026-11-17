# ADR-0007: Detekt with Compose rules (basic config)

- Status: Accepted · 2026-11-16 · amended 2026-11-16

## Decision
- Detekt on every module through `flix.quality`, with `buildUponDefaultConfig = true` and a short `config/detekt/detekt.yml`. **No baseline.**
- The `io.nlopez.compose.rules:detekt` rule set, enabled with defaults. Notable rules: `ModifierMissing`, `ModifierNotUsedAtRoot`, `ViewModelForwarding`, `ViewModelInjection`, `MutableParams`, `UnstableCollections`, `PreviewPublic`, `ComposableNaming`. `@Preview` functions may be private.
- Overrides only where Compose idioms clash with the defaults (for example `FunctionNaming` allows `@Composable` PascalCase, `LongParameterList` ignores `@Composable`).

## Consequences
- + Compose best practices are checked, not just reviewed.
- − Detekt has to be configured per KMP source set. This is handled once in the convention plugin.

## Addendum (2026-11-16): detekt 2.0.0-alpha.6
No stable Detekt supports Kotlin 2.4: the last stable (1.23.8) targets Kotlin 2.0 and is not compatible with Kotlin 2.4 / Gradle 9 / AGP 9's built-in Kotlin compiler. We pin **`dev.detekt:detekt-gradle-plugin` 2.0.0-alpha.6** (new group and plugin id `dev.detekt`, the project's rename ahead of the stable 2.0.0 release), which runs green here with Kotlin 2.4.21, AGP 9.5.1 and Gradle 9.9.1. This is the only non-stable entry in `gradle/libs.versions.toml` (ARCHITECTURE.md §9). Revisit when detekt 2.0.0 is stable.
