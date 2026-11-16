package com.kserocki.flix.app

import android.app.Application
import com.kserocki.flix.app.di.FlixKoinApplication
import org.koin.android.ext.koin.androidContext
import org.koin.plugin.module.dsl.startKoin

/** Composition root: starts Koin with every module registered on [FlixKoinApplication]. */
class FlixApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin<FlixKoinApplication> {
            androidContext(this@FlixApplication)
        }
    }
}
