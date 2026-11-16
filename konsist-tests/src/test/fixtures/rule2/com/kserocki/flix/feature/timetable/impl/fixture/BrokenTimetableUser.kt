package com.kserocki.flix.feature.timetable.impl.fixture

import com.kserocki.flix.feature.other.impl.OtherScreen

/** Violates rule 2: `feature.timetable.impl` must not import another feature's `impl` package. */
class BrokenTimetableUser(private val other: OtherScreen)
