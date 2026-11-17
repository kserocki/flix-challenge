package com.kserocki.flix.app.di

import com.kserocki.flix.core.data.di.DataModule
import com.kserocki.flix.feature.timetable.impl.di.TimetableModule
import org.koin.core.annotation.KoinApplication

/** The Koin graph for [com.kserocki.flix.app.FlixApplication]. */
@KoinApplication(modules = [AppModule::class, DomainModule::class, DataModule::class, TimetableModule::class])
object FlixKoinApplication
