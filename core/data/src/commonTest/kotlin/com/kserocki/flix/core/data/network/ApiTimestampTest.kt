package com.kserocki.flix.core.data.network

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class ApiTimestampTest {

    @Test
    fun `formats an instant as UTC with milliseconds and a Z suffix`() {
        val instant = Instant.parse("2026-03-05T14:11:51.485Z")

        assertEquals("2026-03-05T14:11:51.485Z", instant.toApiTimestamp())
    }

    @Test
    fun `pads whole seconds with zero milliseconds`() {
        val instant = Instant.parse("2026-11-16T16:00:00Z")

        assertEquals("2026-11-16T16:00:00.000Z", instant.toApiTimestamp())
    }

    @Test
    fun `truncates precision below a millisecond`() {
        val instant = Instant.parse("2026-03-05T14:11:51.485999999Z")

        assertEquals("2026-03-05T14:11:51.485Z", instant.toApiTimestamp())
    }
}
