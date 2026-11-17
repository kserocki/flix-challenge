package com.kserocki.flix.core.data.logging

/**
 * A [DataLogger] test double that records every message. Lives in `core:data`'s `commonTest`,
 * not `core:testing`, because `DataLogger` is a `core:data` type (plan #3 §2).
 */
class RecordingDataLogger(override val isEnabled: Boolean = true) : DataLogger {
    private val _messages = mutableListOf<String>()
    val messages: List<String> get() = _messages

    override fun log(message: String) {
        _messages += message
    }
}
