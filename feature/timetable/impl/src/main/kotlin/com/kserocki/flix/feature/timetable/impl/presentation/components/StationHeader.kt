package com.kserocki.flix.feature.timetable.impl.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.kserocki.flix.core.designsystem.theme.FlixSpacing
import com.kserocki.flix.core.designsystem.theme.FlixTheme
import com.kserocki.flix.feature.timetable.impl.R

/** The station's name and the zone its times are shown in (TT-8), the list's first item. */
@Composable
internal fun StationHeader(stationName: String, zoneLabel: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(FlixSpacing.Medium),
    ) {
        Text(text = stationName, style = MaterialTheme.typography.titleMedium)
        Text(
            text = stringResource(R.string.timetable_station_zone, zoneLabel),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = FlixSpacing.ExtraSmall),
        )
    }
}

@PreviewLightDark
@Composable
private fun StationHeaderPreview() {
    FlixTheme {
        Surface {
            StationHeader(
                stationName = "Berlin central bus station",
                zoneLabel = "Europe/Berlin (UTC+1)",
            )
        }
    }
}
