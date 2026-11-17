package com.kserocki.flix.core.data.network

import com.kserocki.flix.core.data.logging.RecordingDataLogger
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class FlixHttpClientTest {

    private fun jsonMockEngine(): MockEngine = MockEngine {
        respond(
            content = "{}",
            status = HttpStatusCode.OK,
            headers = Headers.build { append(HttpHeaders.ContentType, "application/json") },
        )
    }

    @Test
    fun `logs the request when debug logging is enabled`() = runTest {
        val dataLogger = RecordingDataLogger(isEnabled = true)
        val client = createFlixHttpClient(jsonMockEngine(), dataLogger)

        client.get("https://example.test/departures").bodyAsText()

        assertTrue(dataLogger.messages.isNotEmpty())
    }

    @Test
    fun `logs nothing when debug logging is disabled`() = runTest {
        val dataLogger = RecordingDataLogger(isEnabled = false)
        val client = createFlixHttpClient(jsonMockEngine(), dataLogger)

        client.get("https://example.test/departures").bodyAsText()

        assertTrue(dataLogger.messages.isEmpty())
    }
}
