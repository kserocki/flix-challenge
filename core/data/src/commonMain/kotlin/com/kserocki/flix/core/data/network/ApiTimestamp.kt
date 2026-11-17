package com.kserocki.flix.core.data.network

import kotlinx.datetime.LocalDate
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.char
import kotlin.time.Instant

private val API_TIMESTAMP_FORMAT = DateTimeComponents.Format {
    date(LocalDate.Formats.ISO)
    char('T')
    hour()
    char(':')
    minute()
    char(':')
    second()
    char('.')
    secondFraction(3)
    offset(UtcOffset.Formats.ISO)
}

/** ISO-8601 UTC with exactly 3 fraction digits (truncated) and a `Z` suffix (TT-2). */
internal fun Instant.toApiTimestamp(): String = format(API_TIMESTAMP_FORMAT, UtcOffset.ZERO)
