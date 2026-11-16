package com.kserocki.flix.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme()
private val DarkColorScheme = darkColorScheme()

/**
 * The app's Material 3 theme: the baseline light/dark colour schemes, no dynamic colour and no
 * brand colours yet (those come with the timetable screen).
 */
@Composable
fun FlixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content,
    )
}

@PreviewLightDark
@Composable
private fun FlixThemePreview() {
    FlixTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Flix Timetable", style = MaterialTheme.typography.titleLarge)
                Button(onClick = {}) {
                    Text(text = "Sample action")
                }
            }
        }
    }
}
