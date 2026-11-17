package com.kserocki.flix.app.di

import com.kserocki.flix.core.data.di.DataModule
import org.koin.core.annotation.KoinApplication

/**
 * The Koin graph for [com.kserocki.flix.app.FlixApplication]. Later issues add `TimetableModule`
 * here.
 */
@KoinApplication(modules = [AppModule::class, DomainModule::class, DataModule::class])
object FlixKoinApplication
