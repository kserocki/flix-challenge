package com.kserocki.flix.core.domain.error

/** Everything that can go wrong on the boundary, mapped from the data layer (AGENTS.md §4). */
sealed interface DomainError {
    /** No connectivity or a timeout. */
    data object Network : DomainError

    /** An HTTP 5xx or another unexpected status. */
    data class Server(val code: Int) : DomainError

    /** Malformed JSON, an unknown time zone id or a missing mandatory field. */
    data object InvalidData : DomainError
}
