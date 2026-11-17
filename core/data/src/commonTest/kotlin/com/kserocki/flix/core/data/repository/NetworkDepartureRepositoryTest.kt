package com.kserocki.flix.core.data.repository

import com.kserocki.flix.core.common.result.Result
import com.kserocki.flix.core.data.fixture.REAL_DEPARTURES_RESPONSE
import com.kserocki.flix.core.data.fixture.departuresResponseJson
import com.kserocki.flix.core.data.fixture.rideJson
import com.kserocki.flix.core.data.logging.RecordingDataLogger
import com.kserocki.flix.core.data.network.FlixApiClient
import com.kserocki.flix.core.data.network.createFlixHttpClient
import com.kserocki.flix.core.domain.error.DomainError
import com.kserocki.flix.core.domain.model.StationTimetable
import com.kserocki.flix.core.domain.model.TimetableWindow
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

private val A_WINDOW = TimetableWindow(
    from = Instant.parse("2026-11-16T15:00:00Z"),
    to = Instant.parse("2026-11-16T17:00:00Z"),
)

private fun repositoryWith(
    engine: MockEngine,
    logger: RecordingDataLogger = RecordingDataLogger(isEnabled = false),
): NetworkDepartureRepository {
    val httpClient = createFlixHttpClient(engine, logger)
    return NetworkDepartureRepository(FlixApiClient(httpClient), logger)
}

private fun jsonEngine(body: String, status: HttpStatusCode = HttpStatusCode.OK): MockEngine = MockEngine {
    respond(
        content = body,
        status = status,
        headers = Headers.build { append(HttpHeaders.ContentType, "application/json") },
    )
}

class NetworkDepartureRepositoryTest {

    @Test
    fun `returns the station timetable for the real response`() = runTest {
        val repository = repositoryWith(jsonEngine(REAL_DEPARTURES_RESPONSE))

        val result = assertIs<Result.Success<StationTimetable>>(repository.getTimetable(A_WINDOW))

        assertEquals("dcbaaee6-9603-11e6-9066-549f350fcb0c", result.value.station.id)
        assertEquals("Europe/Berlin", result.value.station.timeZone.id)
        assertEquals(
            listOf(
                "96e32f9f-ba2b-4690-affb-12a3caebc06d",
                "3277af6c-c109-40f0-8c57-57af98761198",
                "846fad51-24a3-4ae2-9ac1-e93d8a370a62",
            ),
            result.value.departures.map { it.id },
        )
    }

    @Test
    fun `returns a server error with the status code for a client error`() = runTest {
        val unauthorized = repositoryWith(MockEngine { respondError(HttpStatusCode.Unauthorized) })
        val notFound = repositoryWith(MockEngine { respondError(HttpStatusCode.NotFound) })

        assertEquals(Result.Failure(DomainError.Server(401)), unauthorized.getTimetable(A_WINDOW))
        assertEquals(Result.Failure(DomainError.Server(404)), notFound.getTimetable(A_WINDOW))
    }

    @Test
    fun `returns a server error with the status code for a server error`() = runTest {
        val internalError = repositoryWith(MockEngine { respondError(HttpStatusCode.InternalServerError) })
        val unavailable = repositoryWith(MockEngine { respondError(HttpStatusCode.ServiceUnavailable) })

        assertEquals(Result.Failure(DomainError.Server(500)), internalError.getTimetable(A_WINDOW))
        assertEquals(Result.Failure(DomainError.Server(503)), unavailable.getTimetable(A_WINDOW))
    }

    @Test
    fun `returns a network error when the connection fails`() = runTest {
        val repository = repositoryWith(MockEngine { throw IOException("no connection") })

        assertEquals(Result.Failure(DomainError.Network), repository.getTimetable(A_WINDOW))
    }

    @Test
    fun `returns a network error when the request times out`() = runTest {
        val repository = repositoryWith(MockEngine { throw HttpRequestTimeoutException(it) })

        assertEquals(Result.Failure(DomainError.Network), repository.getTimetable(A_WINDOW))
    }

    @Test
    fun `returns a network error when connecting times out`() = runTest {
        val repository = repositoryWith(
            MockEngine { throw ConnectTimeoutException("connect timed out", Exception("timed out")) },
        )

        assertEquals(Result.Failure(DomainError.Network), repository.getTimetable(A_WINDOW))
    }

    @Test
    fun `returns invalid data for malformed json`() = runTest {
        val repository = repositoryWith(jsonEngine("not json"))

        assertEquals(Result.Failure(DomainError.InvalidData), repository.getTimetable(A_WINDOW))
    }

    @Test
    fun `returns invalid data for a non json response`() = runTest {
        val repository = repositoryWith(
            MockEngine {
                respond(
                    content = "<html></html>",
                    status = HttpStatusCode.OK,
                    headers = Headers.build { append(HttpHeaders.ContentType, "text/html") },
                )
            },
        )

        assertEquals(Result.Failure(DomainError.InvalidData), repository.getTimetable(A_WINDOW))
    }

    @Test
    fun `returns invalid data when the station is missing`() = runTest {
        val repository = repositoryWith(jsonEngine(departuresResponseJson(station = null)))

        assertEquals(Result.Failure(DomainError.InvalidData), repository.getTimetable(A_WINDOW))
    }

    @Test
    fun `returns invalid data when the time zone is missing`() = runTest {
        val stationWithoutTimezone = """{"id":"s-1","name":"Station"}"""
        val repository = repositoryWith(jsonEngine(departuresResponseJson(station = stationWithoutTimezone)))

        assertEquals(Result.Failure(DomainError.InvalidData), repository.getTimetable(A_WINDOW))
    }

    @Test
    fun `returns invalid data when the time zone is unknown`() = runTest {
        val unknownZoneStation = """{"id":"s-1","name":"Station","timezone":"Mars/Base"}"""
        val repository = repositoryWith(jsonEngine(departuresResponseJson(station = unknownZoneStation)))

        assertEquals(Result.Failure(DomainError.InvalidData), repository.getTimetable(A_WINDOW))
    }

    @Test
    fun `returns invalid data when rides are missing`() = runTest {
        val repository = repositoryWith(jsonEngine(departuresResponseJson(rides = null)))

        assertEquals(Result.Failure(DomainError.InvalidData), repository.getTimetable(A_WINDOW))
    }

    @Test
    fun `returns an empty timetable for an empty ride list`() = runTest {
        val repository = repositoryWith(jsonEngine(departuresResponseJson(rides = emptyList())))

        val result = assertIs<Result.Success<StationTimetable>>(repository.getTimetable(A_WINDOW))

        assertTrue(result.value.departures.isEmpty())
    }

    @Test
    fun `skips broken rides and returns the rest`() = runTest {
        val body = departuresResponseJson(rides = listOf(rideJson(id = "ok"), rideJson(id = null)))
        val repository = repositoryWith(jsonEngine(body))

        val result = assertIs<Result.Success<StationTimetable>>(repository.getTimetable(A_WINDOW))

        assertEquals(listOf("ok"), result.value.departures.map { it.id })
    }

    @Test
    fun `logs each skipped ride when debug logging is enabled`() = runTest {
        val logger = RecordingDataLogger(isEnabled = true)
        val body = departuresResponseJson(rides = listOf(rideJson(id = "ok"), rideJson(id = null)))
        val repository = repositoryWith(jsonEngine(body), logger)

        repository.getTimetable(A_WINDOW)

        assertTrue(logger.messages.isNotEmpty())
    }

    @Test
    fun `does not log skipped rides when debug logging is disabled`() = runTest {
        val logger = RecordingDataLogger(isEnabled = false)
        val body = departuresResponseJson(rides = listOf(rideJson(id = "ok"), rideJson(id = null)))
        val repository = repositoryWith(jsonEngine(body), logger)

        repository.getTimetable(A_WINDOW)

        assertTrue(logger.messages.isEmpty())
    }

    @Test
    fun `propagates cancellation instead of returning an error`() = runTest {
        val started = CompletableDeferred<Unit>()
        val repository = repositoryWith(
            MockEngine {
                started.complete(Unit)
                awaitCancellation()
            },
        )
        var result: Result<StationTimetable, DomainError>? = null

        val job = launch { result = repository.getTimetable(A_WINDOW) }
        started.await()
        job.cancelAndJoin()

        assertTrue(job.isCancelled)
        assertNull(result)
    }
}
