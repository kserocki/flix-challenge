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
import com.kserocki.flix.feature.timetable.impl.presentation.TimetableErrorReason

/** The full-screen error (TT-13): a message that matches the error type, and a Retry button. */
@Composable
internal fun TimetableErrorState(reason: TimetableErrorReason, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    val (titleRes, bodyRes) = reason.toMessageResources()
    FlixMessageState(
        title = stringResource(titleRes),
        body = stringResource(bodyRes),
        actionLabel = stringResource(R.string.timetable_action_retry),
        onAction = onRetry,
        modifier = modifier,
    )
}

private fun TimetableErrorReason.toMessageResources(): Pair<Int, Int> = when (this) {
    TimetableErrorReason.Network -> R.string.timetable_error_network_title to R.string.timetable_error_network_body
    TimetableErrorReason.Server -> R.string.timetable_error_server_title to R.string.timetable_error_server_body
    TimetableErrorReason.InvalidData ->
        R.string.timetable_error_invalid_data_title to R.string.timetable_error_invalid_data_body
}

@PreviewLightDark
@Composable
private fun TimetableErrorStateNetworkPreview() {
    FlixTheme {
        Surface {
            TimetableErrorState(reason = TimetableErrorReason.Network, onRetry = {}, modifier = Modifier.fillMaxSize())
        }
    }
}

@PreviewLightDark
@Composable
private fun TimetableErrorStateServerPreview() {
    FlixTheme {
        Surface {
            TimetableErrorState(reason = TimetableErrorReason.Server, onRetry = {}, modifier = Modifier.fillMaxSize())
        }
    }
}

@PreviewLightDark
@Composable
private fun TimetableErrorStateInvalidDataPreview() {
    FlixTheme {
        Surface {
            TimetableErrorState(
                reason = TimetableErrorReason.InvalidData,
                onRetry = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
