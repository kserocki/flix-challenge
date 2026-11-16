package com.kserocki.flix.core.domain.model

import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

/** The time range requested from the repository: `[from, to]` (TT-1). */
data class TimetableWindow(
    val from: Instant,
    val to: Instant,
) {
    companion object {
        private val WINDOW_LENGTH = 2.hours

        /** The board window starting at [now] and covering the next two hours. */
        fun startingAt(now: Instant): TimetableWindow = TimetableWindow(from = now, to = now + WINDOW_LENGTH)
    }
}
