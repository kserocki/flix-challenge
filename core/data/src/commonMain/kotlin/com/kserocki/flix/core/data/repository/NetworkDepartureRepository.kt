package com.kserocki.flix.core.data.repository

import com.kserocki.flix.core.common.result.Result
import com.kserocki.flix.core.common.result.suspendRunCatching
import com.kserocki.flix.core.data.logging.DataLogger
import com.kserocki.flix.core.data.mapper.toDomain
import com.kserocki.flix.core.data.network.FlixApiClient
import com.kserocki.flix.core.data.network.dto.RideDto
import com.kserocki.flix.core.domain.error.DomainError
import com.kserocki.flix.core.domain.model.StationTimetable
import com.kserocki.flix.core.domain.model.TimetableWindow
import com.kserocki.flix.core.domain.repository.DepartureRepository
import org.koin.core.annotation.Single

@Single
internal class NetworkDepartureRepository(
    private val apiClient: FlixApiClient,
    private val logger: DataLogger,
) : DepartureRepository {

    override suspend fun getTimetable(window: TimetableWindow): Result<StationTimetable, DomainError> {
        val response = suspendRunCatching { apiClient.fetchDepartures(window) }
        return when (response) {
            is Result.Success -> response.value.toDomain(onRideSkipped = ::logSkippedRide)
            is Result.Failure -> Result.Failure(response.error.toDomainError())
        }
    }

    private fun logSkippedRide(ride: RideDto) {
        if (logger.isEnabled) {
            logger.log("Skipped ride ${ride.id ?: "<no id>"}: missing or unparsable mandatory field")
        }
    }
}
