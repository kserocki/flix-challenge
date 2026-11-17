package com.kserocki.flix.core.designsystem.theme

import androidx.compose.ui.text.TextStyle

/**
 * Switches the style to tabular (fixed-width) figures, so a column of departure times lines up
 * vertically instead of each digit taking its own natural width.
 */
fun TextStyle.withTabularFigures(): TextStyle = copy(fontFeatureSettings = "tnum")
