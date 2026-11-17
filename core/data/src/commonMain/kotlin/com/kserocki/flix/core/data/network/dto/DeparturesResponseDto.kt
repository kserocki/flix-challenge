package com.kserocki.flix.core.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** The departures endpoint response. `station` and `rides` are mandatory (TT-16b). */
@Serializable
internal data class DeparturesResponseDto(
    val station: StationDto,
    val rides: List<RideDto>,
)

/** All fields are mandatory: a station we can't fully identify can't be shown at all. */
@Serializable
internal data class StationDto(
    val id: String,
    val name: String,
    val timezone: String,
)

/** Every field is nullable so one broken ride doesn't fail the whole response (TT-16). */
@Serializable
internal data class RideDto(
    val id: String? = null,
    val line: LineDto? = null,
    val status: RideStatusDto? = null,
)

@Serializable
internal data class LineDto(
    val code: String? = null,
    val direction: String? = null,
)

@Serializable
internal data class RideStatusDto(
    @SerialName("scheduled_timestamp") val scheduledTimestamp: String? = null,
)
