package com.kserocki.flix.feature.timetable.impl.fixture

import com.kserocki.flix.core.data.remote.DepartureDto

/** Violates rule 3: `feature:*:impl` must not import `core:data`. */
class BrokenTimetableRepository(private val dto: DepartureDto)
