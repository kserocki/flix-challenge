# ADR-0009: Ktor, kotlinx-serialization and typed errors

- Status: Accepted · 2026-11-16

## Decision
- **Ktor client** (OkHttp engine on Android, Darwin on iOS), with `ContentNegotiation` + kotlinx-serialization JSON (`ignoreUnknownKeys = true`, `explicitNulls = false`) and `HttpTimeout`. `Logging` is enabled in debug builds only.
- DTOs declare only the fields we use (`docs/domain/API.md`). Every field a single bad ride could miss is nullable, and the mapper skips that ride (TT-16).
- **Errors as values:** our own `Result<out T, out E>` sealed type in `core:common`, plus `DomainError` in `core:domain`. `kotlin.Result` isn't used, because it erases the error type.
- Exceptions are caught only at the data boundary with `suspendRunCatching`, which rethrows `CancellationException`.
- Tests use Ktor `MockEngine` with JSON fixtures that come from the real response.

## Consequences
- + Exhaustive `when` over errors in the UI. No hidden exceptions.
- − A bit more ceremony than throwing.
