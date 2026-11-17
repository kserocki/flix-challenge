package com.kserocki.flix.core.domain.error

/** Everything that can go wrong on the boundary, mapped from the data layer (AGENTS.md §4). */
sealed interface DomainError {
    /** No connectivity or a timeout. */
    data object Network : DomainError

    /** Any non-2xx HTTP status (4xx/5xx). */
    data class Server(val code: Int) : DomainError

    /**
     * Malformed JSON, an unknown or missing time zone id, a missing mandatory field, every ride
     * skipped (TT-16a), or any other exception the data layer didn't anticipate (a fallback).
     */
    data object InvalidData : DomainError
}
