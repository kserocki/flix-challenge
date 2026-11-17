package com.kserocki.flix.app.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import kotlin.time.Clock

/** Scans `com.kserocki.flix.app` for bindings; also provides the system clock (AGENTS.md §4). */
@Module
@ComponentScan("com.kserocki.flix.app")
class AppModule {

    @Single
    fun clock(): Clock = Clock.System
}
