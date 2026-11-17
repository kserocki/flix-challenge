package com.kserocki.flix.core.data.di

import com.kserocki.flix.core.data.logging.DataLogger
import com.kserocki.flix.core.data.network.createFlixHttpClient
import com.kserocki.flix.core.data.network.platformHttpClientEngine
import io.ktor.client.HttpClient
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.kserocki.flix.core.data")
class DataModule {

    @Single
    internal fun httpClient(logger: DataLogger): HttpClient = createFlixHttpClient(platformHttpClientEngine(), logger)
}
