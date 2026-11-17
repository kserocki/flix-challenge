package com.kserocki.flix.core.data.network

/**
 * The Flix departures API (TT-3): one place for the base URL, the fixed station and the API key.
 * The key is a public constant from the brief, not a secret (`docs/domain/API.md`).
 */
internal object FlixApiConfig {
    const val BASE_URL = "https://global.api.flixbus.com"
    const val STATION_ID = "dcbaaee6-9603-11e6-9066-549f350fcb0c"
    const val API_KEY = "e8d85577-2fb3-4d44-8091-ffb3da46e9ce"
}
