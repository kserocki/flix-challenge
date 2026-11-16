package com.kserocki.flix.core.testing

import com.kserocki.flix.core.domain.model.Departure
import com.kserocki.flix.core.domain.model.Station
import com.kserocki.flix.core.domain.model.StationTimetable
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

/** Defaults come from `docs/domain/departures-response-sample.json`. */
fun aStation(
    id: String = "dcbaaee6-9603-11e6-9066-549f350fcb0c",
    name: String = "Berlin central bus station",
    timeZone: TimeZone = TimeZone.of("Europe/Berlin"),
): Station = Station(id = id, name = name, timeZone = timeZone)

fun aDeparture(
    id: String = "96e32f9f-ba2b-4690-affb-12a3caebc06d",
    lineCode: String = "1322",
    direction: String = "Flensburg central train station",
    scheduledAt: Instant = Instant.parse("2026-11-16T16:00:00Z"),
): Departure = Departure(id = id, lineCode = lineCode, direction = direction, scheduledAt = scheduledAt)

fun aStationTimetable(
    station: Station = aStation(),
    departures: List<Departure> = listOf(aDeparture()),
): StationTimetable = StationTimetable(station = station, departures = departures)
