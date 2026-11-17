package com.kserocki.flix.feature.timetable.impl.presentation

import com.kserocki.flix.core.domain.error.DomainError
import com.kserocki.flix.core.domain.model.Departure
import com.kserocki.flix.core.domain.model.StationTimetable
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.FixedOffsetTimeZone
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.offsetAt
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.time.Instant

private const val SECONDS_PER_HOUR = 3600
private const val SECONDS_PER_MINUTE = 60

private val DEPARTURE_TIME_FORMAT = LocalTime.Format {
    hour()
    char(':')
    minute()
}

/** The station name and the zone label (TT-8), read at the first departure's instant or [now]. */
internal fun StationTimetable.toStationHeader(now: Instant): StationHeaderUiModel {
    val zoneInstant = departures.firstOrNull()?.scheduledAt ?: now
    return StationHeaderUiModel(
        stationName = station.name,
        zoneLabel = station.timeZone.toZoneLabel(zoneInstant),
    )
}

/** The departures as rows, grouped under day headers when the window crosses station midnight (TT-10). */
internal fun StationTimetable.toBoard(now: Instant): TimetableBoard {
    if (departures.isEmpty()) return TimetableBoard.Empty

    val zone = station.timeZone
    val today = now.toLocalDateTime(zone).date
    val departureDates = departures.map { it.scheduledAt.toLocalDateTime(zone).date }
    val showDayHeaders = departureDates.any { it != today }

    val items = mutableListOf<TimetableListItem>()
    var lastDate: LocalDate? = null
    departures.forEachIndexed { index, departure ->
        val date = departureDates[index]
        if (showDayHeaders && date != lastDate) {
            items += TimetableListItem.DayHeader(date.toDayLabel(today))
            lastDate = date
        }
        items += departure.toRow(zone)
    }
    return TimetableBoard.Departures(items.toImmutableList())
}

/** Maps a data-boundary error to its user-facing reason (TT-13). */
internal fun DomainError.toErrorReason(): TimetableErrorReason = when (this) {
    is DomainError.Network -> TimetableErrorReason.Network
    is DomainError.Server -> TimetableErrorReason.Server
    is DomainError.InvalidData -> TimetableErrorReason.InvalidData
}

private fun Departure.toRow(zone: TimeZone): TimetableListItem.DepartureRow =
    TimetableListItem.DepartureRow(
        id = id,
        lineCode = lineCode,
        direction = direction,
        departureTime = DEPARTURE_TIME_FORMAT.format(scheduledAt.toLocalDateTime(zone).time),
    )

private fun LocalDate.toDayLabel(today: LocalDate): DayLabel = when (this) {
    today -> DayLabel.Today
    today.plus(1, DateTimeUnit.DAY) -> DayLabel.Tomorrow
    else -> DayLabel.OtherDay(this)
}

/**
 * `<id> (UTC±offset)`, for example `Europe/Berlin (UTC+1)` (Q1-A). A zone that is a fixed offset,
 * or whose id already equals the offset label (`UTC`), shows the offset label alone.
 */
private fun TimeZone.toZoneLabel(instant: Instant): String {
    val offsetLabel = offsetAt(instant).toOffsetLabel()
    return if (this is FixedOffsetTimeZone || id == offsetLabel) offsetLabel else "$id ($offsetLabel)"
}

private fun UtcOffset.toOffsetLabel(): String {
    if (totalSeconds == 0) return "UTC"
    val sign = if (totalSeconds < 0) '-' else '+'
    val absSeconds = abs(totalSeconds)
    val hours = absSeconds / SECONDS_PER_HOUR
    val minutes = (absSeconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE
    return if (minutes == 0) "UTC$sign$hours" else "UTC$sign$hours:${minutes.toString().padStart(2, '0')}"
}
