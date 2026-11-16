package com.kserocki.flix.core.domain.fixture

/** Violates rule 5b: a `UseCase` with a second public function besides `operator fun invoke`. */
class FooUseCase {
    operator fun invoke(): Int = RESULT

    fun extra(): Int = RESULT

    private companion object {
        private const val RESULT = 0
    }
}
