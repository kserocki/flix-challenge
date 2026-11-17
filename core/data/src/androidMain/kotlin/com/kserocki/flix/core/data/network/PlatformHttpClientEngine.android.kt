package com.kserocki.flix.core.data.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

internal actual fun platformHttpClientEngine(): HttpClientEngine = OkHttp.create()
