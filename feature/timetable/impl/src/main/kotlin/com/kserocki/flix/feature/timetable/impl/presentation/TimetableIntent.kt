package com.kserocki.flix.feature.timetable.impl.presentation

/** User actions [TimetableContent] can send (ADR-0004). Only `TimetableContent` knows this type. */
internal sealed interface TimetableIntent {
    /** The full-screen error's Retry button. */
    data object Retry : TimetableIntent

    /** Pull-to-refresh, the empty state's button, the snackbar action and the a11y action. */
    data object Refresh : TimetableIntent
}
