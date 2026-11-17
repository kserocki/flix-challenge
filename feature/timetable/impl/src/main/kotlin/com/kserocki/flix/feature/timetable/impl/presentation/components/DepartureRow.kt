package com.kserocki.flix.feature.timetable.impl.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.kserocki.flix.core.designsystem.theme.FlixSpacing
import com.kserocki.flix.core.designsystem.theme.FlixTheme
import com.kserocki.flix.core.designsystem.theme.withTabularFigures
import com.kserocki.flix.feature.timetable.impl.R

/**
 * One departure: the direction and line code on the left, the time right-aligned. The row is a
 * single merged semantics node, announced as "Line N160 to Trieste, departs 16:00".
 */
@Composable
internal fun DepartureRow(
    direction: String,
    lineCode: String,
    departureTime: String,
    modifier: Modifier = Modifier,
) {
    val description = stringResource(R.string.timetable_departure_description, lineCode, direction, departureTime)
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = FlixSpacing.Medium, vertical = FlixSpacing.Medium)
                .semantics(mergeDescendants = true) { contentDescription = description },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = direction,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = lineCode,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = departureTime,
                style = MaterialTheme.typography.titleMedium.withTabularFigures(),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = FlixSpacing.Medium),
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@PreviewLightDark
@Composable
private fun DepartureRowPreview() {
    FlixTheme {
        Surface {
            DepartureRow(direction = "Trieste", lineCode = "N160", departureTime = "16:00")
        }
    }
}

@PreviewLightDark
@Composable
private fun DepartureRowLongDirectionPreview() {
    FlixTheme {
        Surface {
            DepartureRow(
                direction = "Munich Central Bus Station via Nuremberg and Regensburg",
                lineCode = "FlixBus 42",
                departureTime = "17:05",
            )
        }
    }
}
