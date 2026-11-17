# ADR-0009: Ktor, kotlinx-serialization and typed errors

- Status: Accepted · 2026-11-16 · amended 2026-11-16

## Decision
- **Ktor client** (OkHttp engine on Android, Darwin on iOS), with `ContentNegotiation` + kotlinx-serialization JSON (`ignoreUnknownKeys = true`, `explicitNulls = false`) and `HttpTimeout`. `Logging` is enabled only when the app-provided `DataLogger.isEnabled` says so (see below).
- DTOs declare only the fields we use (`docs/domain/API.md`). Every field a single bad ride could miss is nullable, and the mapper skips that ride (TT-16).
- **Errors as values:** our own `Result<out T, out E>` sealed type in `core:common`, plus `DomainError` in `core:domain`. `kotlin.Result` isn't used, because it erases the error type.
- Exceptions are caught only at the data boundary with `suspendRunCatching`, which rethrows `CancellationException`.
- Tests use Ktor `MockEngine` with JSON fixtures that come from the real response.

## Debug logging: the app tells the data layer, not `BuildConfig` (amendment, plan #3)
A KMP library has no `BuildConfig`, and iOS has no such concept, so `core:data` can't detect "debug" on its own. It instead exposes one public interface, `DataLogger { val isEnabled: Boolean; fun log(message: String) }`, which the app binds (`LogcatDataLogger`, `isEnabled = BuildConfig.DEBUG`). This is one seam for two debug needs: Ktor `Logging` is installed only if `isEnabled`, and TT-16's skipped-ride logs go through the same logger. A missing binding is a compile-time `KOIN-D001` error (ADR-0003), not a silent no-op. An iOS app would bind its own `DataLogger`.

## Error mapping order (amendment, plan #3)
`NetworkDepartureRepository` maps the caught `Exception` to `DomainError` in this order: `ResponseException` (any non-2xx HTTP status, from Ktor's `expectSuccess = true`) → `Server(code)`; `kotlinx.io.IOException` (timeouts, DNS failures, no connection, Darwin network errors) → `Network`; anything else (malformed JSON, a non-JSON body, a missing mandatory field, …) → `InvalidData` as a fallback, so an exception type we didn't anticipate still becomes a typed, user-facing error instead of crashing.

## The API key and station id live in `core:data` (amendment, plan #3)
`FlixApiConfig` (internal, in `core:data`) holds the base URL, the fixed station id and the API key as constants, not in `app`'s `BuildConfig`. The key is a public constant from the brief, not a secret (it ships in the client either way). Keeping it in `core:data` keeps the multiplatform data layer complete on every platform and keeps TT-3's "one place" from splitting across the app and the data layer.

## Kotlin raw-string fixtures, not resource files (amendment, plan #3)
KMP has no common test-resource API: an Android host test would read a resource from the classpath, while the iOS simulator needs a bundle path, so a shared fixture file would need its own expect/actual loaders for no real gain. The real sample response is instead a Kotlin raw-string constant in `commonTest`, copied verbatim (a subset of rides, trimmed) from `docs/domain/departures-response-sample.json`, so it compiles into every test binary unchanged.

## Consequences
- + Exhaustive `when` over errors in the UI. No hidden exceptions.
- + Release builds pay nothing for HTTP logging; it's compiled in but never installed.
- − A bit more ceremony than throwing.
