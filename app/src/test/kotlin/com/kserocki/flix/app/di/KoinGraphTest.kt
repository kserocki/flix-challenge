package com.kserocki.flix.app.di

import com.kserocki.flix.core.domain.repository.DepartureRepository
import com.kserocki.flix.core.domain.usecase.GetStationTimetableUseCase
import org.koin.core.Koin
import org.koin.plugin.module.dsl.koinApplication
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.time.Clock

/** Resolves the full application graph the way [FlixKoinApplication] assembles it. */
class KoinGraphTest {

    private val koinApp = koinApplication<FlixKoinApplication>()
    private val koin: Koin get() = koinApp.koin

    @AfterTest
    fun tearDown() {
        koinApp.close()
    }

    @Test
    fun `resolves the timetable use case from the application graph`() {
        assertNotNull(koin.get<GetStationTimetableUseCase>())
    }

    @Test
    fun `binds the departure repository`() {
        assertNotNull(koin.get<DepartureRepository>())
    }

    @Test
    fun `provides the system clock`() {
        assertSame(Clock.System, koin.get<Clock>())
    }
}
