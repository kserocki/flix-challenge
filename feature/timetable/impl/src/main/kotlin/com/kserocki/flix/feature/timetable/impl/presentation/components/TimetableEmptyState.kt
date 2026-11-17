package com.kserocki.flix.feature.timetable.impl.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.kserocki.flix.core.designsystem.component.FlixMessageState
import com.kserocki.flix.core.designsystem.theme.FlixTheme
import com.kserocki.flix.feature.timetable.impl.R

/** Zero departures in the window (TT-12): the station name and a Refresh action. */
@Composable
internal fun TimetableEmptyState(stationName: String, onRefresh: () -> Unit, modifier: Modifier = Modifier) {
    FlixMessageState(
        title = stringResource(R.string.timetable_empty_title),
        body = stringResource(R.string.timetable_empty_body, stationName),
        actionLabel = stringResource(R.string.timetable_action_refresh),
        onAction = onRefresh,
        modifier = modifier,
    )
}

@PreviewLightDark
@Composable
private fun TimetableEmptyStatePreview() {
    FlixTheme {
        Surface {
            TimetableEmptyState(
                stationName = "Berlin central bus station",
                onRefresh = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
