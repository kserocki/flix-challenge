package com.kserocki.flix.core.data.mapper

import com.kserocki.flix.core.common.result.Result
import com.kserocki.flix.core.data.network.dto.DeparturesResponseDto
import com.kserocki.flix.core.data.network.dto.RideDto
import com.kserocki.flix.core.domain.error.DomainError
import com.kserocki.flix.core.domain.model.Departure
import com.kserocki.flix.core.domain.model.Station
import com.kserocki.flix.core.domain.model.StationTimetable
import kotlinx.datetime.IllegalTimeZoneException
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

/**
 * Failure(InvalidData) if the station zone can't be resolved (TT-9) or every ride was skipped
 * (TT-16a). Rides missing a mandatory field are skipped and passed to [onRideSkipped] (TT-16).
 * Keeps API order and duplicates: sorting and dedup belong to the use case (TT-5/TT-6).
 */
internal fun DeparturesResponseDto.toDomain(
    onRideSkipped: (RideDto) -> Unit = {},
): Result<StationTimetable, DomainError.InvalidData> {
    val timeZone = station.timezone.toResolvedTimeZoneOrNull()

    val departures = rides.mapNotNull { ride ->
        ride.toDepartureOrNull().also { departure -> if (departure == null) onRideSkipped(ride) }
    }

    val everyRideSkipped = rides.isNotEmpty() && departures.isEmpty()

    return if (timeZone == null || everyRideSkipped) {
        Result.Failure(DomainError.InvalidData)
    } else {
        Result.Success(
            StationTimetable(
                station = Station(id = station.id, name = station.name, timeZone = timeZone),
                departures = departures,
            ),
        )
    }
}

private fun String.toResolvedTimeZoneOrNull(): TimeZone? =
    try {
        TimeZone.of(this)
    } catch (_: IllegalTimeZoneException) {
        null
    }

private fun RideDto.toDepartureOrNull(): Departure? {
    val rideId = id?.takeUnless(String::isBlank)
    val lineCode = line?.code?.takeUnless(String::isBlank)
    val direction = line?.direction?.takeUnless(String::isBlank)
    val scheduledAt = status?.scheduledTimestamp?.let(Instant::parseOrNull)

    return rideId?.let { validId ->
        lineCode?.let { validCode ->
            direction?.let { validDirection ->
                scheduledAt?.let { validTimestamp ->
                    Departure(
                        id = validId,
                        lineCode = validCode,
                        direction = validDirection,
                        scheduledAt = validTimestamp,
                    )
                }
            }
        }
    }
}
