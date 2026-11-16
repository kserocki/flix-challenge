package com.kserocki.flix.core.domain.model

/** The departure board for [station]: [departures] in board order (TT-5/TT-6). */
data class StationTimetable(
    val station: Station,
    val departures: List<Departure>,
)
