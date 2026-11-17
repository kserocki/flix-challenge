package com.kserocki.flix.feature.timetable.impl.presentation

import app.cash.turbine.test
import com.kserocki.flix.core.common.result.Result
import com.kserocki.flix.core.domain.error.DomainError
import com.kserocki.flix.core.domain.usecase.GetStationTimetableUseCase
import com.kserocki.flix.core.testing.FakeDepartureRepository
import com.kserocki.flix.core.testing.TestClock
import com.kserocki.flix.core.testing.aDeparture
import com.kserocki.flix.core.testing.aStationTimetable
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class TimetableViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val now = Instant.parse("2026-11-16T15:00:00Z")

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(repository: FakeDepartureRepository, clock: Clock = TestClock(now)): TimetableViewModel =
        TimetableViewModel(GetStationTimetableUseCase(repository, clock), clock)

    @Test
    fun `shows loading before the first result`() = runTest(dispatcher) {
        val viewModel = viewModel(FakeDepartureRepository())

        assertEquals(TimetableBoard.Loading, viewModel.state.value.board)
    }

    @Test
    fun `shows departures and the station header after a successful first load`() = runTest(dispatcher) {
        val departure = aDeparture()
        val repository = FakeDepartureRepository(
            nextResult = Result.Success(aStationTimetable(departures = listOf(departure))),
        )
        val viewModel = viewModel(repository)

        advanceUntilIdle()

        val board = assertIs<TimetableBoard.Departures>(viewModel.state.value.board)
        assertEquals(1, board.items.size)
        assertEquals("Berlin central bus station", viewModel.state.value.stationHeader?.stationName)
    }

    @Test
    fun `shows the empty board when there are no departures in the window`() = runTest(dispatcher) {
        val repository = FakeDepartureRepository(
            nextResult = Result.Success(aStationTimetable(departures = emptyList())),
        )
        val viewModel = viewModel(repository)

        advanceUntilIdle()

        assertEquals(TimetableBoard.Empty, viewModel.state.value.board)
    }

    @Test
    fun `shows a full screen network error when the first load fails offline`() = runTest(dispatcher) {
        val repository = FakeDepartureRepository(nextResult = Result.Failure(DomainError.Network))
        val viewModel = viewModel(repository)

        advanceUntilIdle()

        assertEquals(TimetableBoard.Error(TimetableErrorReason.Network), viewModel.state.value.board)
    }

    @Test
    fun `shows a full screen server error when the first load fails`() = runTest(dispatcher) {
        val repository = FakeDepartureRepository(nextResult = Result.Failure(DomainError.Server(503)))
        val viewModel = viewModel(repository)

        advanceUntilIdle()

        assertEquals(TimetableBoard.Error(TimetableErrorReason.Server), viewModel.state.value.board)
    }

    @Test
    fun `shows a full screen invalid data error when the first load fails`() = runTest(dispatcher) {
        val repository = FakeDepartureRepository(nextResult = Result.Failure(DomainError.InvalidData))
        val viewModel = viewModel(repository)

        advanceUntilIdle()

        assertEquals(TimetableBoard.Error(TimetableErrorReason.InvalidData), viewModel.state.value.board)
    }

    @Test
    fun `keeps the station header empty after a failed first load`() = runTest(dispatcher) {
        val repository = FakeDepartureRepository(nextResult = Result.Failure(DomainError.Network))
        val viewModel = viewModel(repository)

        advanceUntilIdle()

        assertEquals(null, viewModel.state.value.stationHeader)
    }

    @Test
    fun `retry after a failed first load shows loading and then departures`() = runTest(dispatcher) {
        val repository = FakeDepartureRepository(nextResult = Result.Failure(DomainError.Network))
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        repository.nextResult = Result.Success(aStationTimetable())
        viewModel.onIntent(TimetableIntent.Retry)

        assertEquals(TimetableBoard.Loading, viewModel.state.value.board)

        advanceUntilIdle()

        assertIs<TimetableBoard.Departures>(viewModel.state.value.board)
    }

    @Test
    fun `retry is ignored when departures are shown`() = runTest(dispatcher) {
        val repository = FakeDepartureRepository()
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.onIntent(TimetableIntent.Retry)
        advanceUntilIdle()

        assertEquals(1, repository.requestedWindows.size)
    }

    @Test
    fun `refresh keeps the current departures visible while loading`() = runTest(dispatcher) {
        val departure = aDeparture()
        val repository = FakeDepartureRepository(
            nextResult = Result.Success(aStationTimetable(departures = listOf(departure))),
        )
        val viewModel = viewModel(repository)
        advanceUntilIdle()
        val boardBeforeRefresh = viewModel.state.value.board

        repository.responseGate = CompletableDeferred()
        viewModel.onIntent(TimetableIntent.Refresh)
        advanceUntilIdle()

        assertEquals(boardBeforeRefresh, viewModel.state.value.board)
        assertTrue(viewModel.state.value.isRefreshing)

        repository.responseGate?.complete(Unit)
        advanceUntilIdle()
    }

    @Test
    fun `refresh replaces the departures with the new result`() = runTest(dispatcher) {
        val repository = FakeDepartureRepository(
            nextResult = Result.Success(aStationTimetable(departures = listOf(aDeparture(id = "1")))),
        )
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        repository.nextResult = Result.Success(
            aStationTimetable(departures = listOf(aDeparture(id = "2"), aDeparture(id = "3"))),
        )
        viewModel.onIntent(TimetableIntent.Refresh)
        advanceUntilIdle()

        val board = assertIs<TimetableBoard.Departures>(viewModel.state.value.board)
        assertEquals(2, board.items.size)
        assertEquals(false, viewModel.state.value.isRefreshing)
    }

    @Test
    fun `refresh can turn departures into the empty board`() = runTest(dispatcher) {
        val repository = FakeDepartureRepository(
            nextResult = Result.Success(aStationTimetable(departures = listOf(aDeparture()))),
        )
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        repository.nextResult = Result.Success(aStationTimetable(departures = emptyList()))
        viewModel.onIntent(TimetableIntent.Refresh)
        advanceUntilIdle()

        assertEquals(TimetableBoard.Empty, viewModel.state.value.board)
    }

    @Test
    fun `refresh failure keeps the departures and emits a refresh error effect`() = runTest(dispatcher) {
        val repository = FakeDepartureRepository(
            nextResult = Result.Success(aStationTimetable(departures = listOf(aDeparture()))),
        )
        val viewModel = viewModel(repository)
        advanceUntilIdle()
        val boardBeforeRefresh = viewModel.state.value.board

        viewModel.effects.test {
            repository.nextResult = Result.Failure(DomainError.Network)
            viewModel.onIntent(TimetableIntent.Refresh)
            advanceUntilIdle()

            assertEquals(TimetableEffect.ShowRefreshError(TimetableErrorReason.Network), awaitItem())
        }
        assertEquals(boardBeforeRefresh, viewModel.state.value.board)
        assertEquals(false, viewModel.state.value.isRefreshing)
    }

    @Test
    fun `refresh failure never shows a full screen error`() = runTest(dispatcher) {
        val repository = FakeDepartureRepository(
            nextResult = Result.Success(aStationTimetable(departures = listOf(aDeparture()))),
        )
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        repository.nextResult = Result.Failure(DomainError.Server(500))
        viewModel.onIntent(TimetableIntent.Refresh)
        advanceUntilIdle()

        assertIs<TimetableBoard.Departures>(viewModel.state.value.board)
    }

    @Test
    fun `refresh from the empty board keeps it visible and reloads`() = runTest(dispatcher) {
        val repository = FakeDepartureRepository(
            nextResult = Result.Success(aStationTimetable(departures = emptyList())),
        )
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.onIntent(TimetableIntent.Refresh)
        advanceUntilIdle()

        assertEquals(TimetableBoard.Empty, viewModel.state.value.board)
        assertEquals(2, repository.requestedWindows.size)
    }

    @Test
    fun `refresh requests a new window starting at the current time`() = runTest(dispatcher) {
        val clock = TestClock(now)
        val repository = FakeDepartureRepository()
        val viewModel = viewModel(repository, clock)
        advanceUntilIdle()

        clock.advanceBy(5.minutes)
        viewModel.onIntent(TimetableIntent.Refresh)
        advanceUntilIdle()

        assertEquals(clock.now(), repository.requestedWindows[1].from)
    }

    @Test
    fun `ignores refresh while a load is in progress`() = runTest(dispatcher) {
        val repository = FakeDepartureRepository()
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        repository.responseGate = CompletableDeferred()
        viewModel.onIntent(TimetableIntent.Refresh)
        viewModel.onIntent(TimetableIntent.Refresh)

        repository.responseGate?.complete(Unit)
        advanceUntilIdle()

        assertEquals(2, repository.requestedWindows.size)
    }

    @Test
    fun `does not reload when the state is collected again`() = runTest(dispatcher) {
        val repository = FakeDepartureRepository()
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.state.test { awaitItem() }
        viewModel.state.test { awaitItem() }

        assertEquals(1, repository.requestedWindows.size)
    }

    @Test
    fun `groups departures by station day using the injected clock`() = runTest(dispatcher) {
        val clock = TestClock(Instant.parse("2026-11-16T22:30:00Z"))
        val repository = FakeDepartureRepository(
            nextResult = Result.Success(
                aStationTimetable(
                    departures = listOf(
                        aDeparture(id = "1", scheduledAt = Instant.parse("2026-11-16T22:45:00Z")),
                        aDeparture(id = "2", scheduledAt = Instant.parse("2026-11-16T23:15:00Z")),
                    ),
                ),
            ),
        )
        val viewModel = viewModel(repository, clock)

        advanceUntilIdle()

        val board = assertIs<TimetableBoard.Departures>(viewModel.state.value.board)
        val labels = board.items.filterIsInstance<TimetableListItem.DayHeader>().map { it.label }
        assertEquals(listOf(DayLabel.Today, DayLabel.Tomorrow), labels)
    }
}
