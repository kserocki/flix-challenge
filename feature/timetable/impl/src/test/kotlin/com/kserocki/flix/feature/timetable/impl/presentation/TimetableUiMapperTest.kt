package com.kserocki.flix.feature.timetable.impl.presentation

import com.kserocki.flix.core.domain.error.DomainError
import com.kserocki.flix.core.testing.aDeparture
import com.kserocki.flix.core.testing.aStation
import com.kserocki.flix.core.testing.aStationTimetable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class TimetableUiMapperTest {

    private val deviceDefault = java.util.TimeZone.getDefault()

    @BeforeTest
    fun forceDeviceZoneToNewYork() {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("America/New_York"))
    }

    @AfterTest
    fun restoreDeviceZone() {
        java.util.TimeZone.setDefault(deviceDefault)
    }

    @Test
    fun `formats departure times in the station zone not the device zone`() {
        val timetable = aStationTimetable(
            departures = listOf(aDeparture(scheduledAt = Instant.parse("2026-11-16T16:00:00Z"))),
        )

        val board = timetable.toBoard(now = Instant.parse("2026-11-16T15:00:00Z")) as TimetableBoard.Departures

        assertEquals(listOf("17:00"), board.items.departureTimes())
    }

    @Test
    fun `formats times with leading zeros in 24 hour format`() {
        val timetable = aStationTimetable(
            departures = listOf(
                aDeparture(id = "1", scheduledAt = Instant.parse("2026-11-16T08:05:00Z")),
                aDeparture(id = "2", scheduledAt = Instant.parse("2026-11-16T22:30:00Z")),
            ),
        )

        val board = timetable.toBoard(now = Instant.parse("2026-11-16T07:00:00Z")) as TimetableBoard.Departures

        assertEquals(listOf("09:05", "23:30"), board.items.departureTimes())
    }

    @Test
    fun `maps every departure to a row with line code direction and id`() {
        val departure = aDeparture(id = "ride-1", lineCode = "N160", direction = "Trieste")
        val timetable = aStationTimetable(departures = listOf(departure))

        val board = timetable.toBoard(now = departure.scheduledAt) as TimetableBoard.Departures

        val row = board.items.single() as TimetableListItem.DepartureRow
        assertEquals("ride-1", row.id)
        assertEquals("N160", row.lineCode)
        assertEquals("Trieste", row.direction)
    }

    @Test
    fun `keeps departure order`() {
        val now = Instant.parse("2026-11-16T10:00:00Z")
        val departures = listOf(
            aDeparture(id = "3", scheduledAt = now + 30.minutes),
            aDeparture(id = "1", scheduledAt = now),
            aDeparture(id = "2", scheduledAt = now + 15.minutes),
        )
        val timetable = aStationTimetable(departures = departures)

        val board = timetable.toBoard(now) as TimetableBoard.Departures

        assertEquals(
            listOf("3", "1", "2"),
            board.items.filterIsInstance<TimetableListItem.DepartureRow>().map { it.id },
        )
    }

    @Test
    fun `uses unique keys for every list item`() {
        val now = Instant.parse("2026-11-16T22:30:00Z")
        val timetable = aStationTimetable(
            departures = listOf(
                aDeparture(id = "1", scheduledAt = Instant.parse("2026-11-16T22:45:00Z")),
                aDeparture(id = "2", scheduledAt = Instant.parse("2026-11-16T23:15:00Z")),
            ),
        )

        val board = timetable.toBoard(now) as TimetableBoard.Departures

        val keys = board.items.map { it.key }
        assertEquals(keys.size, keys.toSet().size)
    }

    @Test
    fun `shows no day headers when every departure is on the current station day`() {
        val now = Instant.parse("2026-11-16T10:00:00Z")
        val timetable = aStationTimetable(
            departures = listOf(
                aDeparture(id = "1", scheduledAt = now + 15.minutes),
                aDeparture(id = "2", scheduledAt = now + 30.minutes),
            ),
        )

        val board = timetable.toBoard(now) as TimetableBoard.Departures

        assertEquals(emptyList(), board.items.filterIsInstance<TimetableListItem.DayHeader>())
    }

    @Test
    fun `groups departures under today and tomorrow when the window crosses station midnight`() {
        val now = Instant.parse("2026-11-16T22:30:00Z")
        val timetable = aStationTimetable(
            departures = listOf(
                aDeparture(id = "1", scheduledAt = Instant.parse("2026-11-16T22:45:00Z")),
                aDeparture(id = "2", scheduledAt = Instant.parse("2026-11-16T23:15:00Z")),
            ),
        )

        val board = timetable.toBoard(now) as TimetableBoard.Departures

        assertEquals(
            listOf(
                TimetableListItem.DayHeader(DayLabel.Today),
                TimetableListItem.DepartureRow("1", "1322", "Flensburg central train station", "23:45"),
                TimetableListItem.DayHeader(DayLabel.Tomorrow),
                TimetableListItem.DepartureRow("2", "1322", "Flensburg central train station", "00:15"),
            ),
            board.items,
        )
    }

    @Test
    fun `uses station midnight not device midnight for day headers`() {
        val now = Instant.parse("2026-11-16T22:30:00Z")
        val departure1 = Instant.parse("2026-11-16T22:45:00Z")
        val departure2 = Instant.parse("2026-11-16T23:15:00Z")
        val newYork = TimeZone.of("America/New_York")

        // Sanity check: the device-zone dates are identical, yet the station-zone headers below still split.
        assertEquals(now.toLocalDateTime(newYork).date, departure1.toLocalDateTime(newYork).date)
        assertEquals(now.toLocalDateTime(newYork).date, departure2.toLocalDateTime(newYork).date)

        val timetable = aStationTimetable(
            departures = listOf(
                aDeparture(id = "1", scheduledAt = departure1),
                aDeparture(id = "2", scheduledAt = departure2),
            ),
        )

        val board = timetable.toBoard(now) as TimetableBoard.Departures

        assertEquals(
            listOf(DayLabel.Today, DayLabel.Tomorrow),
            board.items.filterIsInstance<TimetableListItem.DayHeader>().map { it.label },
        )
    }

    @Test
    fun `shows no day headers when crossing device midnight but not station midnight`() {
        val now = Instant.parse("2026-11-17T04:30:00Z")
        val timetable = aStationTimetable(
            departures = listOf(
                aDeparture(id = "1", scheduledAt = Instant.parse("2026-11-17T04:45:00Z")),
                aDeparture(id = "2", scheduledAt = Instant.parse("2026-11-17T05:15:00Z")),
            ),
        )

        val board = timetable.toBoard(now) as TimetableBoard.Departures

        assertEquals(emptyList(), board.items.filterIsInstance<TimetableListItem.DayHeader>())
    }

    @Test
    fun `labels only tomorrow when every departure is after station midnight`() {
        val now = Instant.parse("2026-11-16T22:50:00Z")
        val timetable = aStationTimetable(
            departures = listOf(
                aDeparture(id = "1", scheduledAt = Instant.parse("2026-11-16T23:10:00Z")),
                aDeparture(id = "2", scheduledAt = Instant.parse("2026-11-16T23:40:00Z")),
            ),
        )

        val board = timetable.toBoard(now) as TimetableBoard.Departures

        assertEquals(
            listOf(DayLabel.Tomorrow),
            board.items.filterIsInstance<TimetableListItem.DayHeader>().map { it.label },
        )
    }

    @Test
    fun `labels a departure on another station day with its date`() {
        val now = Instant.parse("2026-11-16T10:00:00Z")
        val timetable = aStationTimetable(
            departures = listOf(aDeparture(scheduledAt = Instant.parse("2026-11-18T10:00:00Z"))),
        )

        val board = timetable.toBoard(now) as TimetableBoard.Departures

        val header = board.items.first() as TimetableListItem.DayHeader
        assertEquals(DayLabel.OtherDay(LocalDate(2026, 11, 18)), header.label)
    }

    @Test
    fun `formats times across the spring daylight saving change`() {
        val timetable = aStationTimetable(
            departures = listOf(
                aDeparture(id = "1", scheduledAt = Instant.parse("2026-03-29T00:30:00Z")),
                aDeparture(id = "2", scheduledAt = Instant.parse("2026-03-29T01:30:00Z")),
            ),
        )

        val board = timetable.toBoard(now = Instant.parse("2026-03-29T00:00:00Z")) as TimetableBoard.Departures

        assertEquals(listOf("01:30", "03:30"), board.items.departureTimes())
    }

    @Test
    fun `formats both repeated hours across the autumn daylight saving change`() {
        val timetable = aStationTimetable(
            departures = listOf(
                aDeparture(id = "1", scheduledAt = Instant.parse("2026-10-25T00:30:00Z")),
                aDeparture(id = "2", scheduledAt = Instant.parse("2026-10-25T01:30:00Z")),
            ),
        )

        val board = timetable.toBoard(now = Instant.parse("2026-10-25T00:00:00Z")) as TimetableBoard.Departures

        assertEquals(listOf("02:30", "02:30"), board.items.departureTimes())
    }

    @Test
    fun `labels the zone with its winter offset at the first departure`() {
        val timetable = aStationTimetable(
            departures = listOf(aDeparture(scheduledAt = Instant.parse("2026-11-16T16:00:00Z"))),
        )

        val header = timetable.toStationHeader(now = Instant.parse("2026-11-16T16:00:00Z"))

        assertEquals("Europe/Berlin (UTC+1)", header.zoneLabel)
    }

    @Test
    fun `labels the zone with its summer offset at the first departure`() {
        val timetable = aStationTimetable(
            departures = listOf(aDeparture(scheduledAt = Instant.parse("2026-07-16T16:00:00Z"))),
        )

        val header = timetable.toStationHeader(now = Instant.parse("2026-07-16T16:00:00Z"))

        assertEquals("Europe/Berlin (UTC+2)", header.zoneLabel)
    }

    @Test
    fun `labels the zone at now when there are no departures`() {
        val timetable = aStationTimetable(departures = emptyList())

        val header = timetable.toStationHeader(now = Instant.parse("2026-07-16T16:00:00Z"))

        assertEquals("Europe/Berlin (UTC+2)", header.zoneLabel)
    }

    @Test
    fun `labels a half hour offset zone with minutes`() {
        val timetable = aStationTimetable(
            station = aStation(name = "Chennai", timeZone = TimeZone.of("Asia/Kolkata")),
            departures = listOf(aDeparture(scheduledAt = Instant.parse("2026-11-16T16:00:00Z"))),
        )

        val header = timetable.toStationHeader(now = Instant.parse("2026-11-16T16:00:00Z"))

        assertEquals("Asia/Kolkata (UTC+5:30)", header.zoneLabel)
    }

    @Test
    fun `labels a utc station zone with the offset only`() {
        val timetable = aStationTimetable(
            station = aStation(timeZone = TimeZone.of("UTC")),
            departures = listOf(aDeparture(scheduledAt = Instant.parse("2026-11-16T16:00:00Z"))),
        )

        val header = timetable.toStationHeader(now = Instant.parse("2026-11-16T16:00:00Z"))

        assertEquals("UTC", header.zoneLabel)
    }

    @Test
    fun `labels a fixed offset station zone with the offset only`() {
        val timetable = aStationTimetable(
            station = aStation(timeZone = TimeZone.of("+01:00")),
            departures = listOf(aDeparture(scheduledAt = Instant.parse("2026-11-16T16:00:00Z"))),
        )

        val header = timetable.toStationHeader(now = Instant.parse("2026-11-16T16:00:00Z"))

        assertEquals("UTC+1", header.zoneLabel)
    }

    @Test
    fun `maps a timetable without departures to the empty board`() {
        val timetable = aStationTimetable(departures = emptyList())

        val board = timetable.toBoard(now = Instant.parse("2026-11-16T16:00:00Z"))

        assertEquals(TimetableBoard.Empty, board)
    }

    @Test
    fun `uses the station name in the header`() {
        val timetable = aStationTimetable(station = aStation(name = "Berlin central bus station"))

        val header = timetable.toStationHeader(now = Instant.parse("2026-11-16T16:00:00Z"))

        assertEquals("Berlin central bus station", header.stationName)
    }

    @Test
    fun `maps a network error to the network reason`() {
        assertEquals(TimetableErrorReason.Network, DomainError.Network.toErrorReason())
    }

    @Test
    fun `maps a server error to the server reason`() {
        assertEquals(TimetableErrorReason.Server, DomainError.Server(503).toErrorReason())
    }

    @Test
    fun `maps an invalid data error to the invalid data reason`() {
        assertEquals(TimetableErrorReason.InvalidData, DomainError.InvalidData.toErrorReason())
    }

    private fun List<TimetableListItem>.departureTimes(): List<String> =
        filterIsInstance<TimetableListItem.DepartureRow>().map { it.departureTime }
}
