package com.kserocki.flix.core.common.result

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SuspendRunCatchingTest {

    @Test
    fun `returns success with the block value`() = runTest {
        val result = suspendRunCatching { 42 }

        assertEquals(Result.Success(42), result)
    }

    @Test
    fun `returns failure with the thrown exception`() = runTest {
        val thrown = IllegalStateException("broken")

        val result = suspendRunCatching<Unit> { throw thrown }

        assertEquals(Result.Failure(thrown), result)
    }

    @Test
    fun `rethrows cancellation exception`() = runTest {
        val cancellation = CancellationException("cancelled")

        val rethrown = assertFailsWith<CancellationException> {
            suspendRunCatching<Unit> { throw cancellation }
        }

        assertEquals(cancellation, rethrown)
    }

    @Test
    fun `does not capture errors`() = runTest {
        assertFailsWith<AssertionError> {
            suspendRunCatching<Unit> { throw AssertionError("boom") }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `cancelling the caller is not swallowed`() = runTest {
        var ranAfterCancellation = false

        val job = launch {
            suspendRunCatching { awaitCancellation() }
            ranAfterCancellation = true
        }
        runCurrent()
        job.cancel()
        job.join()

        assertTrue(job.isCancelled)
        assertFalse(ranAfterCancellation)
    }
}
