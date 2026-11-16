package com.kserocki.flix.core.common.result

import kotlin.test.Test
import kotlin.test.assertEquals

class ResultTest {

    @Test
    fun `map transforms a success`() {
        val result: Result<Int, String> = Result.Success(1)

        val mapped = result.map { it + 1 }

        assertEquals(Result.Success(2), mapped)
    }

    @Test
    fun `map leaves a failure untouched`() {
        val result: Result<Int, String> = Result.Failure("broken")

        val mapped = result.map { it + 1 }

        assertEquals(Result.Failure("broken"), mapped)
    }

    @Test
    fun `mapError transforms a failure`() {
        val result: Result<Int, String> = Result.Failure("broken")

        val mapped = result.mapError { it.length }

        assertEquals(Result.Failure(6), mapped)
    }

    @Test
    fun `mapError leaves a success untouched`() {
        val result: Result<Int, String> = Result.Success(1)

        val mapped = result.mapError { it.length }

        assertEquals(Result.Success(1), mapped)
    }
}
