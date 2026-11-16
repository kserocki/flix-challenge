package com.kserocki.flix.core.domain.model

import kotlinx.datetime.TimeZone

/** A Flix station. Departure times are shown in [timeZone], never the device's (TT-7). */
data class Station(
    val id: String,
    val name: String,
    val timeZone: TimeZone,
)
