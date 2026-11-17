package com.kserocki.flix.core.data.network

import com.kserocki.flix.core.data.fixture.REAL_DEPARTURES_RESPONSE
import com.kserocki.flix.core.data.logging.RecordingDataLogger
import com.kserocki.flix.core.domain.model.TimetableWindow
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

private val A_WINDOW = TimetableWindow(
    from = Instant.parse("2026-11-16T15:00:00Z"),
    to = Instant.parse("2026-11-16T17:00:00Z"),
)

class FlixApiClientTest {

    private fun jsonMockEngine(body: String, capturedUrls: MutableList<Url>): MockEngine =
        MockEngine { request ->
            capturedUrls += request.url
            respond(
                content = body,
                status = HttpStatusCode.OK,
                headers = Headers.build { append(HttpHeaders.ContentType, "application/json") },
            )
        }

    private fun apiClient(engine: MockEngine): FlixApiClient =
        FlixApiClient(createFlixHttpClient(engine, RecordingDataLogger(isEnabled = false)))

    @Test
    fun `requests the departures of the configured station`() = runTest {
        val urls = mutableListOf<Url>()
        val client = apiClient(jsonMockEngine(REAL_DEPARTURES_RESPONSE, urls))

        client.fetchDepartures(A_WINDOW)

        val requestedUrlWithoutQuery = urls.single().toString().substringBefore('?')
        assertEquals(
            "https://global.api.flixbus.com/gis/v2/timetable/dcbaaee6-9603-11e6-9066-549f350fcb0c/departures",
            requestedUrlWithoutQuery,
        )
    }

    @Test
    fun `sends the window as UTC timestamps with milliseconds`() = runTest {
        val urls = mutableListOf<Url>()
        val client = apiClient(jsonMockEngine(REAL_DEPARTURES_RESPONSE, urls))

        client.fetchDepartures(A_WINDOW)

        val parameters = urls.single().parameters
        assertEquals("2026-11-16T15:00:00.000Z", parameters["from"])
        assertEquals("2026-11-16T17:00:00.000Z", parameters["to"])
    }

    @Test
    fun `sends the api key`() = runTest {
        val urls = mutableListOf<Url>()
        val client = apiClient(jsonMockEngine(REAL_DEPARTURES_RESPONSE, urls))

        client.fetchDepartures(A_WINDOW)

        assertEquals("e8d85577-2fb3-4d44-8091-ffb3da46e9ce", urls.single().parameters["apiKey"])
    }

    @Test
    fun `decodes the real response and ignores unknown fields`() = runTest {
        val client = apiClient(jsonMockEngine(REAL_DEPARTURES_RESPONSE, mutableListOf()))

        val response = client.fetchDepartures(A_WINDOW)

        assertEquals("dcbaaee6-9603-11e6-9066-549f350fcb0c", response.station.id)
        assertEquals(3, response.rides.size)
    }
}
