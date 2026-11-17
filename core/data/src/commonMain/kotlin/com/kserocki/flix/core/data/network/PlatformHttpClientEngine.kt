package com.kserocki.flix.core.data.network

import io.ktor.client.engine.HttpClientEngine

/** The Ktor engine for this platform: OkHttp on Android, Darwin on iOS. */
internal expect fun platformHttpClientEngine(): HttpClientEngine
