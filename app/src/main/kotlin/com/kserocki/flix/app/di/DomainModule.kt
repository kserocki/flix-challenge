package com.kserocki.flix.app.di

import com.kserocki.flix.core.domain.repository.DepartureRepository
import com.kserocki.flix.core.domain.usecase.GetStationTimetableUseCase
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import kotlin.time.Clock

/** Use cases are plain classes in the Koin-free domain, so they are provided here (ADR-0003, option D). */
@Module
class DomainModule {

    @Factory
    fun getStationTimetableUseCase(repository: DepartureRepository, clock: Clock): GetStationTimetableUseCase =
        GetStationTimetableUseCase(repository, clock)
}
