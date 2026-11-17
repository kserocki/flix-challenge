package com.kserocki.flix.core.data.network

import com.kserocki.flix.core.data.network.dto.DeparturesResponseDto
import com.kserocki.flix.core.domain.model.TimetableWindow
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.koin.core.annotation.Single

@Single
internal class FlixApiClient(private val httpClient: HttpClient) {

    /** `GET {BASE_URL}/gis/v2/timetable/{STATION_ID}/departures?from&to&apiKey` (TT-2, TT-3). */
    suspend fun fetchDepartures(window: TimetableWindow): DeparturesResponseDto {
        val url = "${FlixApiConfig.BASE_URL}/gis/v2/timetable/${FlixApiConfig.STATION_ID}/departures"
        return httpClient.get(url) {
            parameter("from", window.from.toApiTimestamp())
            parameter("to", window.to.toApiTimestamp())
            parameter("apiKey", FlixApiConfig.API_KEY)
        }.body()
    }
}
