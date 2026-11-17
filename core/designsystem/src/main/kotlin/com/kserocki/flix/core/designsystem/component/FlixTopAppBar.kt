package com.kserocki.flix.core.designsystem.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.kserocki.flix.core.designsystem.theme.FlixTheme

/** The app's top bar: a static title on the brand green container, with the status bar insets. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlixTopAppBar(title: String, modifier: Modifier = Modifier) {
    TopAppBar(
        title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    )
}

@PreviewLightDark
@Composable
private fun FlixTopAppBarPreview() {
    FlixTheme {
        FlixTopAppBar(title = "Departures")
    }
}
