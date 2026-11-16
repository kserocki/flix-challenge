package com.kserocki.flix.core.domain.usecase

import com.kserocki.flix.core.common.result.Result
import com.kserocki.flix.core.domain.error.DomainError
import com.kserocki.flix.core.domain.model.Departure
import com.kserocki.flix.core.domain.model.TimetableWindow
import com.kserocki.flix.core.testing.FakeDepartureRepository
import com.kserocki.flix.core.testing.TestClock
import com.kserocki.flix.core.testing.aDeparture
import com.kserocki.flix.core.testing.aStation
import com.kserocki.flix.core.testing.aStationTimetable
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class GetStationTimetableUseCaseTest {

    private val now = Instant.parse("2026-11-16T15:00:00Z")

    @Test
    fun `requests the window from now to two hours later`() = runTest {
        val repository = FakeDepartureRepository()
        val useCase = GetStationTimetableUseCase(repository, TestClock(now))

        useCase()

        assertEquals(listOf(TimetableWindow(now, now + 2.hours)), repository.requestedWindows)
    }

    @Test
    fun `reads the clock again on every call`() = runTest {
        val repository = FakeDepartureRepository()
        val clock = TestClock(now)
        val useCase = GetStationTimetableUseCase(repository, clock)

        useCase()
        clock.advanceBy(5.minutes)
        useCase()

        val laterNow = now + 5.minutes
        assertEquals(
            listOf(TimetableWindow(now, now + 2.hours), TimetableWindow(laterNow, laterNow + 2.hours)),
            repository.requestedWindows,
        )
    }

    @Test
    fun `sorts departures by scheduled time`() = runTest {
        val early = aDeparture(id = "1", scheduledAt = now)
        val late = aDeparture(id = "2", scheduledAt = now + 1.hours)
        val repository = departureRepositoryReturning(late, early)
        val useCase = GetStationTimetableUseCase(repository, TestClock(now))

        val result = useCase().successValue()

        assertEquals(listOf(early, late), result.departures)
    }

    @Test
    fun `keeps API order for departures scheduled at the same time`() = runTest {
        val first = aDeparture(id = "1", lineCode = "100", scheduledAt = now)
        val second = aDeparture(id = "2", lineCode = "200", scheduledAt = now)
        val repository = departureRepositoryReturning(first, second)
        val useCase = GetStationTimetableUseCase(repository, TestClock(now))

        val result = useCase().successValue()

        assertEquals(listOf(first, second), result.departures)
    }

    @Test
    fun `shows a ride with a repeated id only once`() = runTest {
        val departure = aDeparture(id = "1")
        val repeated = departure.copy(lineCode = "999")
        val repository = departureRepositoryReturning(departure, repeated)
        val useCase = GetStationTimetableUseCase(repository, TestClock(now))

        val result = useCase().successValue()

        assertEquals(1, result.departures.size)
    }

    @Test
    fun `keeps the first occurrence of a repeated ride`() = runTest {
        val first = aDeparture(id = "1", lineCode = "100")
        val repeated = aDeparture(id = "1", lineCode = "999")
        val repository = departureRepositoryReturning(first, repeated)
        val useCase = GetStationTimetableUseCase(repository, TestClock(now))

        val result = useCase().successValue()

        assertEquals(listOf(first), result.departures)
    }

    @Test
    fun `returns the station unchanged`() = runTest {
        val station = aStation(id = "station-1", name = "Test station")
        val repository = FakeDepartureRepository(
            nextResult = Result.Success(aStationTimetable(station = station, departures = emptyList())),
        )
        val useCase = GetStationTimetableUseCase(repository, TestClock(now))

        val result = useCase().successValue()

        assertEquals(station, result.station)
    }

    @Test
    fun `returns an empty timetable when there are no departures`() = runTest {
        val repository = FakeDepartureRepository(
            nextResult = Result.Success(aStationTimetable(departures = emptyList())),
        )
        val useCase = GetStationTimetableUseCase(repository, TestClock(now))

        val result = useCase().successValue()

        assertEquals(emptyList(), result.departures)
    }

    @Test
    fun `passes a network error through`() = runTest {
        val repository = FakeDepartureRepository(nextResult = Result.Failure(DomainError.Network))
        val useCase = GetStationTimetableUseCase(repository, TestClock(now))

        val result = useCase()

        assertEquals(Result.Failure(DomainError.Network), result)
    }

    @Test
    fun `passes a server error through with its code`() = runTest {
        val repository = FakeDepartureRepository(nextResult = Result.Failure(DomainError.Server(503)))
        val useCase = GetStationTimetableUseCase(repository, TestClock(now))

        val result = useCase()

        assertEquals(Result.Failure(DomainError.Server(503)), result)
    }

    @Test
    fun `passes an invalid data error through`() = runTest {
        val repository = FakeDepartureRepository(nextResult = Result.Failure(DomainError.InvalidData))
        val useCase = GetStationTimetableUseCase(repository, TestClock(now))

        val result = useCase()

        assertEquals(Result.Failure(DomainError.InvalidData), result)
    }

    private fun departureRepositoryReturning(vararg departures: Departure) =
        FakeDepartureRepository(nextResult = Result.Success(aStationTimetable(departures = departures.toList())))

    private fun <T, E> Result<T, E>.successValue(): T =
        when (this) {
            is Result.Success -> value
            is Result.Failure -> error("expected a success but got $this")
        }
}
