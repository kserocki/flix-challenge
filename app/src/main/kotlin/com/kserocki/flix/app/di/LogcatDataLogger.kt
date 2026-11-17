package com.kserocki.flix.app.di

import android.util.Log
import com.kserocki.flix.app.BuildConfig
import com.kserocki.flix.core.data.logging.DataLogger
import org.koin.core.annotation.Single

private const val TAG = "FlixData"

/** The app's [DataLogger] binding: debug-only, writing to Logcat. */
@Single
internal class LogcatDataLogger : DataLogger {
    override val isEnabled: Boolean = BuildConfig.DEBUG

    override fun log(message: String) {
        Log.d(TAG, message)
    }
}
