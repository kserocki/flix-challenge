package com.kserocki.flix.feature.timetable.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kserocki.flix.core.common.result.Result
import com.kserocki.flix.core.domain.model.StationTimetable
import com.kserocki.flix.core.domain.usecase.GetStationTimetableUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import kotlin.time.Clock

/**
 * Loads the board on creation (TT-15) and reduces [TimetableIntent]s into [state] (ADR-0004). At
 * most one load runs at a time: an intent received while [loadJob] is active is ignored.
 */
@KoinViewModel
internal class TimetableViewModel(
    private val getStationTimetable: GetStationTimetableUseCase,
    private val clock: Clock,
) : ViewModel() {

    private val mutableState = MutableStateFlow(TimetableUiState())
    val state: StateFlow<TimetableUiState> = mutableState.asStateFlow()

    private val effectChannel = Channel<TimetableEffect>(Channel.BUFFERED)
    val effects: Flow<TimetableEffect> = effectChannel.receiveAsFlow()

    private var loadJob: Job? = null

    init {
        load(isRefresh = false)
    }

    fun onIntent(intent: TimetableIntent) {
        if (loadJob?.isActive == true) return
        when (intent) {
            TimetableIntent.Retry -> retry()
            TimetableIntent.Refresh -> refresh()
        }
    }

    private fun retry() {
        if (mutableState.value.board !is TimetableBoard.Error) return
        mutableState.update { it.copy(board = TimetableBoard.Loading) }
        load(isRefresh = false)
    }

    private fun refresh() {
        val board = mutableState.value.board
        if (board !is TimetableBoard.Departures && board !is TimetableBoard.Empty) return
        mutableState.update { it.copy(isRefreshing = true) }
        load(isRefresh = true)
    }

    private fun load(isRefresh: Boolean) {
        loadJob = viewModelScope.launch {
            when (val result = getStationTimetable()) {
                is Result.Success -> onLoadSuccess(result.value)
                is Result.Failure -> onLoadFailure(result.error.toErrorReason(), isRefresh)
            }
        }
    }

    private fun onLoadSuccess(timetable: StationTimetable) {
        val now = clock.now()
        mutableState.update {
            it.copy(
                stationHeader = timetable.toStationHeader(now),
                board = timetable.toBoard(now),
                isRefreshing = false,
            )
        }
    }

    private suspend fun onLoadFailure(reason: TimetableErrorReason, isRefresh: Boolean) {
        if (isRefresh) {
            mutableState.update { it.copy(isRefreshing = false) }
            effectChannel.send(TimetableEffect.ShowRefreshError(reason))
        } else {
            mutableState.update { it.copy(board = TimetableBoard.Error(reason)) }
        }
    }
}
