package com.kserocki.flix.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.kserocki.flix.core.designsystem.theme.FlixSpacing
import com.kserocki.flix.core.designsystem.theme.FlixTheme

/**
 * A centred title, body and single action, for the empty and full-screen error states. The
 * column scrolls vertically so long text at font scale 2.0 never clips. Sizing is left to the
 * caller: give it a bounded height (`Modifier.fillMaxSize()`, or `fillParentMaxSize()` for a
 * `LazyColumn` item), otherwise the scroll modifier has nothing to measure against.
 */
@Composable
fun FlixMessageState(
    title: String,
    body: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(FlixSpacing.Large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = FlixSpacing.Small, bottom = FlixSpacing.Large),
        )
        Button(onClick = onAction) {
            Text(text = actionLabel)
        }
    }
}

@PreviewLightDark
@Composable
private fun FlixMessageStatePreview() {
    FlixTheme {
        Surface {
            FlixMessageState(
                title = "No connection",
                body = "Check your internet connection and try again.",
                actionLabel = "Retry",
                onAction = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
