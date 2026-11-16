package com.kserocki.flix.core.common.result

/**
 * Our own result type for repository and use-case boundaries (AGENTS.md §4): no exceptions cross
 * a layer boundary, and `kotlin.Result` can't carry a typed error.
 */
sealed interface Result<out T, out E> {
    data class Success<out T>(val value: T) : Result<T, Nothing>
    data class Failure<out E>(val error: E) : Result<Nothing, E>
}

inline fun <T, E, R> Result<T, E>.map(transform: (T) -> R): Result<R, E> =
    when (this) {
        is Result.Success -> Result.Success(transform(value))
        is Result.Failure -> this
    }

inline fun <T, E, F> Result<T, E>.mapError(transform: (E) -> F): Result<T, F> =
    when (this) {
        is Result.Success -> this
        is Result.Failure -> Result.Failure(transform(error))
    }
