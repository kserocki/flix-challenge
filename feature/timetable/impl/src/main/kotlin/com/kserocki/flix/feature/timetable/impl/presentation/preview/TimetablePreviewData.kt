package com.kserocki.flix.feature.timetable.impl.presentation.preview

import com.kserocki.flix.feature.timetable.impl.presentation.DayLabel
import com.kserocki.flix.feature.timetable.impl.presentation.StationHeaderUiModel
import com.kserocki.flix.feature.timetable.impl.presentation.TimetableListItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** Fixture data shared by every `@Preview` in this package, so the states look consistent. */
internal object TimetablePreviewData {
    val stationHeader = StationHeaderUiModel(
        stationName = "Berlin central bus station",
        zoneLabel = "Europe/Berlin (UTC+1)",
    )

    val departures: ImmutableList<TimetableListItem> = persistentListOf(
        TimetableListItem.DepartureRow(id = "1", lineCode = "N160", direction = "Trieste", departureTime = "16:00"),
        TimetableListItem.DepartureRow(
            id = "2",
            lineCode = "FlixBus 42",
            direction = "Munich Central Bus Station",
            departureTime = "16:20",
        ),
        TimetableListItem.DepartureRow(id = "3", lineCode = "N900", direction = "Hamburg ZOB", departureTime = "17:10"),
    )

    val departuresWithDayHeaders: ImmutableList<TimetableListItem> = persistentListOf(
        TimetableListItem.DayHeader(DayLabel.Today),
        TimetableListItem.DepartureRow(id = "1", lineCode = "N160", direction = "Trieste", departureTime = "23:45"),
        TimetableListItem.DayHeader(DayLabel.Tomorrow),
        TimetableListItem.DepartureRow(id = "2", lineCode = "N900", direction = "Hamburg ZOB", departureTime = "00:15"),
    )
}
