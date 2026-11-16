package com.kserocki.flix.core.domain.repository

import com.kserocki.flix.core.common.result.Result
import com.kserocki.flix.core.domain.error.DomainError
import com.kserocki.flix.core.domain.model.StationTimetable
import com.kserocki.flix.core.domain.model.TimetableWindow

interface DepartureRepository {
    /** The configured station's timetable for [window]; the station id is data-layer config (TT-3). */
    suspend fun getTimetable(window: TimetableWindow): Result<StationTimetable, DomainError>
}
