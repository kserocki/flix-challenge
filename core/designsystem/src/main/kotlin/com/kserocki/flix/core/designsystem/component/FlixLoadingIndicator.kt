package com.kserocki.flix.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.kserocki.flix.core.designsystem.theme.FlixTheme

/**
 * A centred loading spinner, announced through [contentDescription]. Sizing is left to the
 * caller: pass `Modifier.fillMaxSize()` to centre it in a whole screen.
 */
@Composable
fun FlixLoadingIndicator(contentDescription: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.semantics(mergeDescendants = true) { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@PreviewLightDark
@Composable
private fun FlixLoadingIndicatorPreview() {
    FlixTheme {
        Surface {
            FlixLoadingIndicator(
                contentDescription = "Loading departures",
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
