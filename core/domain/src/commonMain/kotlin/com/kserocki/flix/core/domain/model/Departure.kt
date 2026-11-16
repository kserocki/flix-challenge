package com.kserocki.flix.core.domain.model

import kotlin.time.Instant

/** One scheduled ride shown on the board. */
data class Departure(
    val id: String,
    val lineCode: String,
    val direction: String,
    val scheduledAt: Instant,
)
