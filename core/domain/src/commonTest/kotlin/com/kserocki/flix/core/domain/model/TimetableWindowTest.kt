package com.kserocki.flix.core.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

class TimetableWindowTest {

    @Test
    fun `starts at the given instant`() {
        val now = Instant.parse("2026-11-16T16:00:00Z")

        val window = TimetableWindow.startingAt(now)

        assertEquals(now, window.from)
    }

    @Test
    fun `ends exactly two hours after the start`() {
        val now = Instant.parse("2026-11-16T16:00:00Z")

        val window = TimetableWindow.startingAt(now)

        assertEquals(now + 2.hours, window.to)
    }
}
