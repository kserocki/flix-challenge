package com.kserocki.flix.feature.timetable.impl.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.kserocki.flix.core.designsystem.theme.FlixSpacing
import com.kserocki.flix.core.designsystem.theme.FlixTheme

/** A day group's label (TT-10), for example "Today" or "Tomorrow". Marked as a heading for TalkBack. */
@Composable
internal fun DayHeader(label: String, modifier: Modifier = Modifier) {
    Text(
        text = label,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = FlixSpacing.Medium, vertical = FlixSpacing.Small)
            .semantics { heading() },
    )
}

@PreviewLightDark
@Composable
private fun DayHeaderPreview() {
    FlixTheme {
        Surface {
            DayHeader(label = "Tomorrow")
        }
    }
}
