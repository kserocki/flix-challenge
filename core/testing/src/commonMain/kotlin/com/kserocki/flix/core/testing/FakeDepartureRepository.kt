package com.kserocki.flix.core.testing

import com.kserocki.flix.core.common.result.Result
import com.kserocki.flix.core.domain.error.DomainError
import com.kserocki.flix.core.domain.model.StationTimetable
import com.kserocki.flix.core.domain.model.TimetableWindow
import com.kserocki.flix.core.domain.repository.DepartureRepository

/** A fake [DepartureRepository] that always returns [nextResult] and records every requested window. */
class FakeDepartureRepository(
    var nextResult: Result<StationTimetable, DomainError> = Result.Success(aStationTimetable()),
) : DepartureRepository {

    private val _requestedWindows = mutableListOf<TimetableWindow>()
    val requestedWindows: List<TimetableWindow> get() = _requestedWindows

    override suspend fun getTimetable(window: TimetableWindow): Result<StationTimetable, DomainError> {
        _requestedWindows += window
        return nextResult
    }
}
