package com.kserocki.flix.feature.timetable.impl.presentation

/** One-off events the ViewModel sends once, through a buffered channel (ADR-0004). */
internal sealed interface TimetableEffect {
    /** A refresh failed while the board stayed visible; shown as a snackbar with Retry (TT-14). */
    data class ShowRefreshError(val reason: TimetableErrorReason) : TimetableEffect
}
