package com.kserocki.flix.core.data.mapper

import com.kserocki.flix.core.common.result.Result
import com.kserocki.flix.core.data.network.dto.DeparturesResponseDto
import com.kserocki.flix.core.data.network.dto.LineDto
import com.kserocki.flix.core.data.network.dto.RideDto
import com.kserocki.flix.core.data.network.dto.RideStatusDto
import com.kserocki.flix.core.data.network.dto.StationDto
import com.kserocki.flix.core.domain.error.DomainError
import com.kserocki.flix.core.domain.model.StationTimetable
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Instant

private fun aStationDto(
    id: String = "dcbaaee6-9603-11e6-9066-549f350fcb0c",
    name: String = "Berlin central bus station",
    timezone: String = "Europe/Berlin",
): StationDto = StationDto(id = id, name = name, timezone = timezone)

private fun validRideDto(
    id: String = "96e32f9f-ba2b-4690-affb-12a3caebc06d",
    code: String = "1322",
    direction: String = "Flensburg central train station",
    scheduledTimestamp: String = "2026-11-16T16:00:00Z",
): RideDto = RideDto(
    id = id,
    line = LineDto(code = code, direction = direction),
    status = RideStatusDto(scheduledTimestamp = scheduledTimestamp),
)

private fun aResponseDto(
    station: StationDto = aStationDto(),
    rides: List<RideDto> = listOf(validRideDto()),
): DeparturesResponseDto = DeparturesResponseDto(station = station, rides = rides)

class DeparturesResponseMapperTest {

    @Test
    fun `maps the station id name and time zone`() {
        val response = aResponseDto(
            station = aStationDto(id = "s-1", name = "Berlin central bus station", timezone = "Europe/Berlin"),
        )

        val result = assertIs<Result.Success<StationTimetable>>(response.toDomain())

        assertEquals("s-1", result.value.station.id)
        assertEquals("Berlin central bus station", result.value.station.name)
        assertEquals(TimeZone.of("Europe/Berlin"), result.value.station.timeZone)
    }

    @Test
    fun `maps a complete ride to a departure`() {
        val response = aResponseDto(
            rides = listOf(
                validRideDto(
                    id = "96e32f9f-ba2b-4690-affb-12a3caebc06d",
                    code = "1322",
                    direction = "Flensburg central train station",
                    scheduledTimestamp = "2026-11-16T16:00:00Z",
                ),
            ),
        )

        val result = assertIs<Result.Success<StationTimetable>>(response.toDomain())

        val departure = result.value.departures.single()
        assertEquals("96e32f9f-ba2b-4690-affb-12a3caebc06d", departure.id)
        assertEquals("1322", departure.lineCode)
        assertEquals("Flensburg central train station", departure.direction)
        assertEquals(Instant.parse("2026-11-16T16:00:00Z"), departure.scheduledAt)
    }

    @Test
    fun `keeps a numeric-looking line code as text`() {
        val response = aResponseDto(rides = listOf(validRideDto(code = "050")))

        val result = assertIs<Result.Success<StationTimetable>>(response.toDomain())

        assertEquals("050", result.value.departures.single().lineCode)
    }

    @Test
    fun `parses a scheduled timestamp without milliseconds`() {
        val response = aResponseDto(rides = listOf(validRideDto(scheduledTimestamp = "2026-11-16T16:00:00Z")))

        val result = assertIs<Result.Success<StationTimetable>>(response.toDomain())

        assertEquals(Instant.parse("2026-11-16T16:00:00Z"), result.value.departures.single().scheduledAt)
    }

    @Test
    fun `parses a scheduled timestamp with milliseconds`() {
        val response = aResponseDto(rides = listOf(validRideDto(scheduledTimestamp = "2026-11-16T16:00:00.123Z")))

        val result = assertIs<Result.Success<StationTimetable>>(response.toDomain())

        assertEquals(Instant.parse("2026-11-16T16:00:00.123Z"), result.value.departures.single().scheduledAt)
    }

    @Test
    fun `keeps API order and repeated ids`() {
        val first = validRideDto(id = "a", scheduledTimestamp = "2026-11-16T17:00:00Z")
        val second = validRideDto(id = "b", scheduledTimestamp = "2026-11-16T16:00:00Z")
        val third = validRideDto(id = "a", scheduledTimestamp = "2026-11-16T18:00:00Z")
        val response = aResponseDto(rides = listOf(first, second, third))

        val result = assertIs<Result.Success<StationTimetable>>(response.toDomain())

        assertEquals(listOf("a", "b", "a"), result.value.departures.map { it.id })
    }

    @Test
    fun `returns invalid data for an unknown time zone id`() {
        val response = aResponseDto(station = aStationDto(timezone = "Mars/Base"))

        assertEquals(Result.Failure(DomainError.InvalidData), response.toDomain())
    }

    @Test
    fun `returns invalid data for a blank time zone id`() {
        val response = aResponseDto(station = aStationDto(timezone = ""))

        assertEquals(Result.Failure(DomainError.InvalidData), response.toDomain())
    }

    @Test
    fun `skips a ride without an id`() {
        val response = aResponseDto(rides = listOf(validRideDto(id = "ok"), validRideDto().copy(id = null)))

        val result = assertIs<Result.Success<StationTimetable>>(response.toDomain())

        assertEquals(listOf("ok"), result.value.departures.map { it.id })
    }

    @Test
    fun `skips a ride without a line`() {
        val response = aResponseDto(rides = listOf(validRideDto(id = "ok"), validRideDto().copy(line = null)))

        val result = assertIs<Result.Success<StationTimetable>>(response.toDomain())

        assertEquals(listOf("ok"), result.value.departures.map { it.id })
    }

    @Test
    fun `skips a ride without a line code`() {
        val ride = validRideDto().let { it.copy(line = it.line?.copy(code = null)) }
        val response = aResponseDto(rides = listOf(validRideDto(id = "ok"), ride))

        val result = assertIs<Result.Success<StationTimetable>>(response.toDomain())

        assertEquals(listOf("ok"), result.value.departures.map { it.id })
    }

    @Test
    fun `skips a ride with a blank line code`() {
        val ride = validRideDto().let { it.copy(line = it.line?.copy(code = "   ")) }
        val response = aResponseDto(rides = listOf(validRideDto(id = "ok"), ride))

        val result = assertIs<Result.Success<StationTimetable>>(response.toDomain())

        assertEquals(listOf("ok"), result.value.departures.map { it.id })
    }

    @Test
    fun `skips a ride without a direction`() {
        val ride = validRideDto().let { it.copy(line = it.line?.copy(direction = null)) }
        val response = aResponseDto(rides = listOf(validRideDto(id = "ok"), ride))

        val result = assertIs<Result.Success<StationTimetable>>(response.toDomain())

        assertEquals(listOf("ok"), result.value.departures.map { it.id })
    }

    @Test
    fun `skips a ride with a blank direction`() {
        val ride = validRideDto().let { it.copy(line = it.line?.copy(direction = "   ")) }
        val response = aResponseDto(rides = listOf(validRideDto(id = "ok"), ride))

        val result = assertIs<Result.Success<StationTimetable>>(response.toDomain())

        assertEquals(listOf("ok"), result.value.departures.map { it.id })
    }

    @Test
    fun `skips a ride without a status`() {
        val response = aResponseDto(rides = listOf(validRideDto(id = "ok"), validRideDto().copy(status = null)))

        val result = assertIs<Result.Success<StationTimetable>>(response.toDomain())

        assertEquals(listOf("ok"), result.value.departures.map { it.id })
    }

    @Test
    fun `skips a ride without a scheduled timestamp`() {
        val ride = validRideDto().let { it.copy(status = it.status?.copy(scheduledTimestamp = null)) }
        val response = aResponseDto(rides = listOf(validRideDto(id = "ok"), ride))

        val result = assertIs<Result.Success<StationTimetable>>(response.toDomain())

        assertEquals(listOf("ok"), result.value.departures.map { it.id })
    }

    @Test
    fun `skips a ride with an unparsable scheduled timestamp`() {
        val ride = validRideDto().let { it.copy(status = it.status?.copy(scheduledTimestamp = "not-a-timestamp")) }
        val response = aResponseDto(rides = listOf(validRideDto(id = "ok"), ride))

        val result = assertIs<Result.Success<StationTimetable>>(response.toDomain())

        assertEquals(listOf("ok"), result.value.departures.map { it.id })
    }

    @Test
    fun `reports every skipped ride`() {
        val brokenRide = validRideDto().copy(id = null)
        val anotherBrokenRide = validRideDto().copy(status = null)
        val response = aResponseDto(rides = listOf(validRideDto(), brokenRide, anotherBrokenRide))
        val skipped = mutableListOf<RideDto>()

        response.toDomain(onRideSkipped = { skipped += it })

        assertEquals(listOf(brokenRide, anotherBrokenRide), skipped)
    }

    @Test
    fun `returns an empty timetable when there are no rides`() {
        val response = aResponseDto(rides = emptyList())

        val result = assertIs<Result.Success<StationTimetable>>(response.toDomain())

        assertTrue(result.value.departures.isEmpty())
    }

    @Test
    fun `returns invalid data when every ride is skipped`() {
        val response = aResponseDto(rides = listOf(validRideDto().copy(id = null), validRideDto().copy(status = null)))

        assertEquals(Result.Failure(DomainError.InvalidData), response.toDomain())
    }
}
