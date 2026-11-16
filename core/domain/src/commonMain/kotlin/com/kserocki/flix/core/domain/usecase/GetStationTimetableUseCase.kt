package com.kserocki.flix.core.domain.usecase

import com.kserocki.flix.core.common.result.Result
import com.kserocki.flix.core.common.result.map
import com.kserocki.flix.core.domain.error.DomainError
import com.kserocki.flix.core.domain.model.Departure
import com.kserocki.flix.core.domain.model.StationTimetable
import com.kserocki.flix.core.domain.model.TimetableWindow
import com.kserocki.flix.core.domain.repository.DepartureRepository
import kotlin.time.Clock

/** Loads the board for the next two hours from `now` (TT-1), deduped and sorted (TT-5/TT-6). */
class GetStationTimetableUseCase(
    private val repository: DepartureRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(): Result<StationTimetable, DomainError> {
        val window = TimetableWindow.startingAt(clock.now())
        return repository.getTimetable(window).map { timetable ->
            timetable.copy(departures = timetable.departures.inBoardOrder())
        }
    }
}

/** Keeps the first occurrence of a repeated id (TT-6), then stable-sorts by scheduled time (TT-5). */
private fun List<Departure>.inBoardOrder(): List<Departure> =
    distinctBy(Departure::id).sortedBy(Departure::scheduledAt)
