# ADR-0007: Detekt with Compose rules (basic config)

- Status: Accepted · 2026-11-16

## Decision
- Detekt on every module through `flix.quality`, with `buildUponDefaultConfig = true` and a short `config/detekt/detekt.yml`. **No baseline.**
- The `io.nlopez.compose.rules:detekt` rule set, enabled with defaults. Notable rules: `ModifierMissing`, `ModifierNotUsedAtRoot`, `ViewModelForwarding`, `ViewModelInjection`, `MutableParams`, `UnstableCollections`, `PreviewPublic`, `ComposableNaming`. `@Preview` functions may be private.
- Overrides only where Compose idioms clash with the defaults (for example `FunctionNaming` allows `@Composable` PascalCase, `LongParameterList` ignores `@Composable`).

## Consequences
- + Compose best practices are checked, not just reviewed.
- − Detekt has to be configured per KMP source set. This is handled once in the convention plugin.
