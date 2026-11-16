package com.kserocki.flix.app.di

import org.koin.core.annotation.KoinApplication

/**
 * The Koin graph for [com.kserocki.flix.app.FlixApplication]. Later issues add `DataModule` and
 * `TimetableModule` here.
 */
@KoinApplication(modules = [AppModule::class])
object FlixKoinApplication
