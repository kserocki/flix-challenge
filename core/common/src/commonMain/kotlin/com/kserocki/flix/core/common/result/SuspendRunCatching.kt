package com.kserocki.flix.core.common.result

import kotlin.coroutines.cancellation.CancellationException

/**
 * [Result.Success] with the block's value, or [Result.Failure] with any [Exception] the block
 * throws. [CancellationException] is rethrown, never wrapped, so structured concurrency still
 * sees the cancellation. [Error]s are not caught and propagate.
 */
// The one boundary helper allowed to catch Exception; CancellationException is rethrown above it.
@Suppress("TooGenericExceptionCaught")
suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T, Exception> =
    try {
        Result.Success(block())
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (exception: Exception) {
        Result.Failure(exception)
    }
