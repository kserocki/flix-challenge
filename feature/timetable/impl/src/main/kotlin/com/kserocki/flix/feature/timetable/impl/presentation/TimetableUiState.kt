package com.kserocki.flix.feature.timetable.impl.presentation

import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.LocalDate

/** Everything [com.kserocki.flix.feature.timetable.impl.presentation.TimetableContent] renders (ADR-0004). */
internal data class TimetableUiState(
    val stationHeader: StationHeaderUiModel? = null,
    val board: TimetableBoard = TimetableBoard.Loading,
    val isRefreshing: Boolean = false,
)

/** The board's content area. [Loading] and [Error] are full screen (TT-11, TT-13). */
internal sealed interface TimetableBoard {
    data object Loading : TimetableBoard

    data class Departures(val items: ImmutableList<TimetableListItem>) : TimetableBoard

    data object Empty : TimetableBoard

    data class Error(val reason: TimetableErrorReason) : TimetableBoard
}

/** The station name and the zone its times are shown in (TT-8). */
internal data class StationHeaderUiModel(
    val stationName: String,
    val zoneLabel: String,
)

/** One row of the departure list: a [DayHeader] (TT-10) or a [DepartureRow]. */
internal sealed interface TimetableListItem {
    val key: String

    data class DayHeader(val label: DayLabel) : TimetableListItem {
        override val key: String get() = "day-$label"
    }

    data class DepartureRow(
        val id: String,
        val lineCode: String,
        val direction: String,
        val departureTime: String,
    ) : TimetableListItem {
        override val key: String get() = "departure-$id"
    }
}

/** A day header's label, relative to the station-local date of `now` (TT-10). */
internal sealed interface DayLabel {
    data object Today : DayLabel

    data object Tomorrow : DayLabel

    data class OtherDay(val date: LocalDate) : DayLabel
}

/** A [com.kserocki.flix.core.domain.error.DomainError] mapped to a user-facing reason (TT-13). */
internal enum class TimetableErrorReason { Network, Server, InvalidData }
