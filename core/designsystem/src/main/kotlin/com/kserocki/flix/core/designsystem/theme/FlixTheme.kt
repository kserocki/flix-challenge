package com.kserocki.flix.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark

/** The app's Material 3 theme: the Flix brand colour schemes (Q7 of issue #4), no dynamic colour. */
@Composable
fun FlixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) FlixDarkColorScheme else FlixLightColorScheme,
        content = content,
    )
}

@PreviewLightDark
@Composable
private fun FlixThemeTokenShowcasePreview() {
    FlixTheme {
        Surface {
            Column(modifier = Modifier.padding(FlixSpacing.Medium)) {
                Text(text = "Flix Timetable", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "Departs 16:00",
                    style = MaterialTheme.typography.titleMedium.withTabularFigures(),
                )
                Text(
                    text = "N160 to Trieste",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
