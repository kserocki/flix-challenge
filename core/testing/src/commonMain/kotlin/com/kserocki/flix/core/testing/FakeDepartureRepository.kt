package com.kserocki.flix.core.testing

import com.kserocki.flix.core.common.result.Result
import com.kserocki.flix.core.domain.error.DomainError
import com.kserocki.flix.core.domain.model.StationTimetable
import com.kserocki.flix.core.domain.model.TimetableWindow
import com.kserocki.flix.core.domain.repository.DepartureRepository
import kotlinx.coroutines.CompletableDeferred

/** A fake [DepartureRepository] that always returns [nextResult] and records every requested window. */
class FakeDepartureRepository(
    var nextResult: Result<StationTimetable, DomainError> = Result.Success(aStationTimetable()),
) : DepartureRepository {

    private val _requestedWindows = mutableListOf<TimetableWindow>()
    val requestedWindows: List<TimetableWindow> get() = _requestedWindows

    /** When set, [getTimetable] suspends until it completes, after recording the window. */
    var responseGate: CompletableDeferred<Unit>? = null

    override suspend fun getTimetable(window: TimetableWindow): Result<StationTimetable, DomainError> {
        _requestedWindows += window
        responseGate?.await()
        return nextResult
    }
}
