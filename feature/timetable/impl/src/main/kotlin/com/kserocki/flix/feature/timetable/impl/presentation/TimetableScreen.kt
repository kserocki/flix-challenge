package com.kserocki.flix.feature.timetable.impl.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.kserocki.flix.core.designsystem.component.FlixLoadingIndicator
import com.kserocki.flix.core.designsystem.component.FlixTopAppBar
import com.kserocki.flix.feature.timetable.impl.R
import com.kserocki.flix.feature.timetable.impl.presentation.components.DayHeader
import com.kserocki.flix.feature.timetable.impl.presentation.components.DepartureRow
import com.kserocki.flix.feature.timetable.impl.presentation.components.StationHeader
import com.kserocki.flix.feature.timetable.impl.presentation.components.TimetableEmptyState
import com.kserocki.flix.feature.timetable.impl.presentation.components.TimetableErrorState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.toJavaLocalDate
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/** Stateful entry point: collects [TimetableViewModel.state] and shows [TimetableEffect]s as a snackbar. */
@Composable
internal fun TimetableScreen(modifier: Modifier = Modifier, viewModel: TimetableViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current
    val retryActionLabel = stringResource(R.string.timetable_action_retry)
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel, snackbarHostState, lifecycleOwner, resources, retryActionLabel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            // collectLatest: an effect that arrives while a snackbar is still showing replaces it
            // immediately (cancelling the suspended showSnackbar call dismisses its snackbar).
            viewModel.effects.collectLatest { effect ->
                val message = when (effect) {
                    is TimetableEffect.ShowRefreshError -> resources.getString(effect.reason.toRefreshErrorStringRes())
                }
                val result = snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = retryActionLabel,
                    duration = SnackbarDuration.Long,
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.onIntent(TimetableIntent.Refresh)
                }
            }
        }
    }

    // A refresh that succeeds sends no effect, so a stale refresh-error snackbar from an earlier
    // failure wouldn't otherwise be replaced; dismiss it as soon as a new refresh starts.
    LaunchedEffect(state.isRefreshing, snackbarHostState) {
        if (state.isRefreshing) {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    TimetableContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

private fun TimetableErrorReason.toRefreshErrorStringRes(): Int = when (this) {
    TimetableErrorReason.Network -> R.string.timetable_refresh_error_network
    TimetableErrorReason.Server -> R.string.timetable_refresh_error_server
    TimetableErrorReason.InvalidData -> R.string.timetable_refresh_error_invalid_data
}

/** Stateless content: the only composable here that knows [TimetableIntent] (ADR-0004). */
@Composable
internal fun TimetableContent(
    state: TimetableUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (TimetableIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val layoutDirection = LocalLayoutDirection.current
    Scaffold(
        modifier = modifier,
        topBar = { FlixTopAppBar(title = stringResource(R.string.timetable_title)) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        when (val board = state.board) {
            TimetableBoard.Loading -> FlixLoadingIndicator(
                contentDescription = stringResource(R.string.timetable_loading),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )

            is TimetableBoard.Error -> TimetableErrorState(
                reason = board.reason,
                onRetry = { onIntent(TimetableIntent.Retry) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )

            is TimetableBoard.Departures -> TimetableBoardList(
                stationHeader = state.stationHeader,
                isRefreshing = state.isRefreshing,
                contentPadding = innerPadding.toListContentPadding(layoutDirection),
                onRefresh = { onIntent(TimetableIntent.Refresh) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()),
            ) {
                items(
                    board.items,
                    key = { it.key },
                    contentType = { it.contentType() },
                ) { item -> TimetableListItemRow(item) }
            }

            TimetableBoard.Empty -> TimetableBoardList(
                stationHeader = state.stationHeader,
                isRefreshing = state.isRefreshing,
                contentPadding = innerPadding.toListContentPadding(layoutDirection),
                onRefresh = { onIntent(TimetableIntent.Refresh) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()),
            ) {
                item(key = "empty", contentType = "empty") {
                    TimetableEmptyState(
                        stationName = state.stationHeader?.stationName.orEmpty(),
                        onRefresh = { onIntent(TimetableIntent.Refresh) },
                        modifier = Modifier.fillParentMaxSize(),
                    )
                }
            }
        }
    }
}

private fun TimetableListItem.contentType(): String = when (this) {
    is TimetableListItem.DayHeader -> "day-header"
    is TimetableListItem.DepartureRow -> "departure-row"
}

@Composable
private fun TimetableListItemRow(item: TimetableListItem) {
    when (item) {
        is TimetableListItem.DayHeader -> DayHeader(label = item.label.toText())
        is TimetableListItem.DepartureRow -> DepartureRow(
            direction = item.direction,
            lineCode = item.lineCode,
            departureTime = item.departureTime,
        )
    }
}

/** The shared pull-to-refresh list shell for the [TimetableBoard.Departures]/[TimetableBoard.Empty] boards. */
@Composable
private fun TimetableBoardList(
    stationHeader: StationHeaderUiModel?,
    isRefreshing: Boolean,
    contentPadding: PaddingValues,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit,
) {
    val refreshAccessibilityLabel = stringResource(R.string.timetable_refresh_accessibility_action)
    val pullToRefreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        modifier = modifier,
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter),
                color = MaterialTheme.colorScheme.primary,
            )
        },
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = contentPadding) {
            stationHeader?.let { header ->
                item(key = "station-header", contentType = "station-header") {
                    StationHeader(
                        stationName = header.stationName,
                        zoneLabel = header.zoneLabel,
                        modifier = Modifier.semantics(mergeDescendants = true) {
                            heading()
                            customActions = listOf(
                                CustomAccessibilityAction(refreshAccessibilityLabel) {
                                    onRefresh()
                                    true
                                },
                            )
                        },
                    )
                }
            }
            content()
        }
    }
}

private fun PaddingValues.toListContentPadding(layoutDirection: LayoutDirection): PaddingValues = PaddingValues(
    start = calculateStartPadding(layoutDirection),
    end = calculateEndPadding(layoutDirection),
    bottom = calculateBottomPadding(),
)

@Composable
private fun DayLabel.toText(): String = when (this) {
    DayLabel.Today -> stringResource(R.string.timetable_day_today)
    DayLabel.Tomorrow -> stringResource(R.string.timetable_day_tomorrow)
    is DayLabel.OtherDay -> {
        val formatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }
        date.toJavaLocalDate().format(formatter)
    }
}
