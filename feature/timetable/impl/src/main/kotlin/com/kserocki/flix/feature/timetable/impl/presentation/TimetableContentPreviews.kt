package com.kserocki.flix.feature.timetable.impl.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.kserocki.flix.core.designsystem.theme.FlixTheme
import com.kserocki.flix.feature.timetable.impl.presentation.preview.TimetablePreviewData

@PreviewLightDark
@Composable
private fun TimetableContentLoadingPreview() {
    TimetableContentPreview(TimetableUiState(board = TimetableBoard.Loading))
}

@PreviewLightDark
@Composable
private fun TimetableContentPreview() {
    TimetableContentPreview(
        TimetableUiState(
            stationHeader = TimetablePreviewData.stationHeader,
            board = TimetableBoard.Departures(TimetablePreviewData.departures),
        ),
    )
}

@PreviewLightDark
@Composable
private fun TimetableContentWithDayHeadersPreview() {
    TimetableContentPreview(
        TimetableUiState(
            stationHeader = TimetablePreviewData.stationHeader,
            board = TimetableBoard.Departures(TimetablePreviewData.departuresWithDayHeaders),
        ),
    )
}

@PreviewLightDark
@Composable
private fun TimetableContentRefreshingPreview() {
    TimetableContentPreview(
        TimetableUiState(
            stationHeader = TimetablePreviewData.stationHeader,
            board = TimetableBoard.Departures(TimetablePreviewData.departures),
            isRefreshing = true,
        ),
    )
}

@PreviewLightDark
@Composable
private fun TimetableContentEmptyPreview() {
    TimetableContentPreview(
        TimetableUiState(stationHeader = TimetablePreviewData.stationHeader, board = TimetableBoard.Empty),
    )
}

@PreviewLightDark
@Composable
private fun TimetableContentNetworkErrorPreview() {
    TimetableContentPreview(TimetableUiState(board = TimetableBoard.Error(TimetableErrorReason.Network)))
}

@PreviewLightDark
@Composable
private fun TimetableContentServerErrorPreview() {
    TimetableContentPreview(TimetableUiState(board = TimetableBoard.Error(TimetableErrorReason.Server)))
}

@PreviewLightDark
@Composable
private fun TimetableContentInvalidDataErrorPreview() {
    TimetableContentPreview(TimetableUiState(board = TimetableBoard.Error(TimetableErrorReason.InvalidData)))
}

@Preview(fontScale = 2f)
@Composable
private fun TimetableContentFontScale2Preview() {
    TimetableContentPreview(
        TimetableUiState(
            stationHeader = TimetablePreviewData.stationHeader,
            board = TimetableBoard.Departures(TimetablePreviewData.departures),
        ),
    )
}

@Composable
private fun TimetableContentPreview(state: TimetableUiState) {
    FlixTheme {
        Surface {
            TimetableContent(
                state = state,
                snackbarHostState = remember { SnackbarHostState() },
                onIntent = {},
            )
        }
    }
}
