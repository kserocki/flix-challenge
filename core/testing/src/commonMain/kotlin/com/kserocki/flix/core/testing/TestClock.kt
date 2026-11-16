package com.kserocki.flix.core.testing

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

/** A [Clock] a test controls: it never changes on its own, only via [advanceBy]. */
class TestClock(initial: Instant) : Clock {
    private var current: Instant = initial

    override fun now(): Instant = current

    fun advanceBy(duration: Duration) {
        current += duration
    }
}
