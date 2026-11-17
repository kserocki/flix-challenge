package com.kserocki.flix.core.data.repository

import com.kserocki.flix.core.domain.error.DomainError
import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException

/**
 * [ResponseException] (non-2xx) → [DomainError.Server]; any [IOException] (timeouts, no
 * connection, Darwin errors) → [DomainError.Network]; anything else (malformed JSON, a
 * non-JSON body, …) → [DomainError.InvalidData] as a fallback (Q3).
 */
internal fun Exception.toDomainError(): DomainError =
    when (this) {
        is ResponseException -> DomainError.Server(response.status.value)
        is IOException -> DomainError.Network
        else -> DomainError.InvalidData
    }
