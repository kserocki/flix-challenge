package com.kserocki.flix.core.data.logging

/**
 * A [DataLogger] test double that records every message. Lives in `core:data`'s `commonTest`,
 * not `core:testing`, because `DataLogger` is a `core:data` type: `core:testing` is also used by
 * `feature:*:impl`, which must not depend on `core:data` (AGENTS.md §3 rule 3).
 */
class RecordingDataLogger(override val isEnabled: Boolean = true) : DataLogger {
    private val _messages = mutableListOf<String>()
    val messages: List<String> get() = _messages

    override fun log(message: String) {
        _messages += message
    }
}
