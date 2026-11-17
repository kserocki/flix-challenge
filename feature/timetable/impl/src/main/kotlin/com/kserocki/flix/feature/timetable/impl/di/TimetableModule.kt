package com.kserocki.flix.feature.timetable.impl.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/** Scans `com.kserocki.flix.feature.timetable.impl` for `@KoinViewModel` bindings (ADR-0003). */
@Module
@ComponentScan("com.kserocki.flix.feature.timetable.impl")
class TimetableModule
