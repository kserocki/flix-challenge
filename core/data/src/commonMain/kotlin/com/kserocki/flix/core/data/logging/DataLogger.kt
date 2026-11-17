package com.kserocki.flix.core.data.logging

/** Diagnostics sink for the data layer, bound by the app; the KMP library has no BuildConfig. */
interface DataLogger {
    /** True only in debug builds: enables HTTP logging and skipped-ride logs (TT-16). */
    val isEnabled: Boolean

    fun log(message: String)
}
