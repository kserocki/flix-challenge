package com.kserocki.flix.feature.timetable.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kserocki.flix.feature.timetable.api.TimetableNavKey
import com.kserocki.flix.feature.timetable.impl.presentation.TimetableScreen

/** Registers the timetable screen on the app's `entryProvider` (ADR-0006). */
fun EntryProviderScope<NavKey>.timetableEntry() {
    entry<TimetableNavKey> { TimetableScreen() }
}
