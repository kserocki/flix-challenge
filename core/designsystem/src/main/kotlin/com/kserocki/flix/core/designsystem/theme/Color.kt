package com.kserocki.flix.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Flix brand colour tokens (Q7 of issue #4's plan). The light scheme uses Flix green on a near
 * white neutral, the dark scheme a deep green on a near black neutral. Every text-on-background
 * pair here was checked to at least 4.5:1 (WCAG AA); `error` keeps Material's own baseline red
 * instead of the brand hue, so the "something is wrong" colour stays universally recognisable.
 *
 * `primaryLight`/`onPrimaryContainerLight` and the window backgrounds **must** match
 * `core/designsystem/src/main/res/values{,-night}/colors.xml` (`flix_launcher_background`,
 * `flix_launcher_foreground`, `flix_window_background`): those XML colours paint the launcher
 * icon and the cold-start window, which can't read a Compose token.
 */
private object FlixColorTokens {
    // Brand green: the top bar container and the launcher icon background/foreground.
    val PrimaryLight = Color(0xFF2E6A00)
    val OnPrimaryLight = Color(0xFFFFFFFF)
    val PrimaryContainerLight = Color(0xFF73D700)
    val OnPrimaryContainerLight = Color(0xFF132300)

    val PrimaryDark = Color(0xFF73D700)
    val OnPrimaryDark = Color(0xFF132300)
    val PrimaryContainerDark = Color(0xFF1F3A08)
    val OnPrimaryContainerDark = Color(0xFFD2F5AE)

    // Secondary: a desaturated step of the same green hue, for lower-emphasis accents.
    val SecondaryLight = Color(0xFF475D3C)
    val OnSecondaryLight = Color(0xFFFFFFFF)
    val SecondaryContainerLight = Color(0xFFDFE4DD)
    val OnSecondaryContainerLight = Color(0xFF212C1C)

    val SecondaryDark = Color(0xFFC4D1BD)
    val OnSecondaryDark = Color(0xFF263220)
    val SecondaryContainerDark = Color(0xFF3F4B3A)
    val OnSecondaryContainerDark = Color(0xFFE5E8E3)

    // Tertiary: a complementary blue accent, kept for a second accent role if one is ever needed.
    val TertiaryLight = Color(0xFF324467)
    val OnTertiaryLight = Color(0xFFFFFFFF)
    val TertiaryContainerLight = Color(0xFFDBDEE6)
    val OnTertiaryContainerLight = Color(0xFF172030)

    val TertiaryDark = Color(0xFFB7C2D7)
    val OnTertiaryDark = Color(0xFF1B2437)
    val TertiaryContainerDark = Color(0xFF343E50)
    val OnTertiaryContainerDark = Color(0xFFE1E4EA)

    // Neutral surface: a faint green tint so greys don't clash with the brand hue.
    val BackgroundLight = Color(0xFFF7FBF1)
    val OnBackgroundLight = Color(0xFF1A1C18)
    val SurfaceLight = Color(0xFFF7FBF1)
    val OnSurfaceLight = Color(0xFF1A1C18)
    val SurfaceVariantLight = Color(0xFFEAEDE8)
    val OnSurfaceVariantLight = Color(0xFF44483E)
    val OutlineLight = Color(0xFF727F6C)
    val OutlineVariantLight = Color(0xFFCFD7CC)

    val BackgroundDark = Color(0xFF121411)
    val OnBackgroundDark = Color(0xFFE2E3DC)
    val SurfaceDark = Color(0xFF121411)
    val OnSurfaceDark = Color(0xFFE2E3DC)
    val SurfaceVariantDark = Color(0xFF3B4337)
    val OnSurfaceVariantDark = Color(0xFFC4C8BA)
    val OutlineDark = Color(0xFF96A191)
    val OutlineVariantDark = Color(0xFF4A5445)

    // Surface container ramp (used by PullToRefreshBox's indicator, among others).
    val SurfaceDimLight = Color(0xFFDDE1DB)
    val SurfaceBrightLight = Color(0xFFFBFCFB)
    val SurfaceContainerLowestLight = Color(0xFFFFFFFF)
    val SurfaceContainerLowLight = Color(0xFFF3F5F2)
    val SurfaceContainerLight = Color(0xFFEEF0ED)
    val SurfaceContainerHighLight = Color(0xFFE9EBE7)
    val SurfaceContainerHighestLight = Color(0xFFE3E7E2)

    val SurfaceDimDark = Color(0xFF0D0D0C)
    val SurfaceBrightDark = Color(0xFF3C403B)
    val SurfaceContainerLowestDark = Color(0xFF090909)
    val SurfaceContainerLowDark = Color(0xFF171816)
    val SurfaceContainerDark = Color(0xFF1D1E1C)
    val SurfaceContainerHighDark = Color(0xFF2A2C28)
    val SurfaceContainerHighestDark = Color(0xFF363935)

    // Inverse roles (used by the default Snackbar): each theme mirrors the other theme's surface
    // and primary, which is both the idiomatic M3 pairing and guarantees the same contrast ratios
    // already checked above.
    val InverseSurfaceLight = SurfaceDark
    val InverseOnSurfaceLight = OnSurfaceDark
    val InversePrimaryLight = PrimaryDark

    val InverseSurfaceDark = SurfaceLight
    val InverseOnSurfaceDark = OnSurfaceLight
    val InversePrimaryDark = PrimaryLight
}

internal val FlixLightColorScheme: ColorScheme = lightColorScheme(
    primary = FlixColorTokens.PrimaryLight,
    onPrimary = FlixColorTokens.OnPrimaryLight,
    primaryContainer = FlixColorTokens.PrimaryContainerLight,
    onPrimaryContainer = FlixColorTokens.OnPrimaryContainerLight,
    inversePrimary = FlixColorTokens.InversePrimaryLight,
    secondary = FlixColorTokens.SecondaryLight,
    onSecondary = FlixColorTokens.OnSecondaryLight,
    secondaryContainer = FlixColorTokens.SecondaryContainerLight,
    onSecondaryContainer = FlixColorTokens.OnSecondaryContainerLight,
    tertiary = FlixColorTokens.TertiaryLight,
    onTertiary = FlixColorTokens.OnTertiaryLight,
    tertiaryContainer = FlixColorTokens.TertiaryContainerLight,
    onTertiaryContainer = FlixColorTokens.OnTertiaryContainerLight,
    background = FlixColorTokens.BackgroundLight,
    onBackground = FlixColorTokens.OnBackgroundLight,
    surface = FlixColorTokens.SurfaceLight,
    onSurface = FlixColorTokens.OnSurfaceLight,
    surfaceVariant = FlixColorTokens.SurfaceVariantLight,
    onSurfaceVariant = FlixColorTokens.OnSurfaceVariantLight,
    outline = FlixColorTokens.OutlineLight,
    outlineVariant = FlixColorTokens.OutlineVariantLight,
    inverseSurface = FlixColorTokens.InverseSurfaceLight,
    inverseOnSurface = FlixColorTokens.InverseOnSurfaceLight,
    surfaceDim = FlixColorTokens.SurfaceDimLight,
    surfaceBright = FlixColorTokens.SurfaceBrightLight,
    surfaceContainerLowest = FlixColorTokens.SurfaceContainerLowestLight,
    surfaceContainerLow = FlixColorTokens.SurfaceContainerLowLight,
    surfaceContainer = FlixColorTokens.SurfaceContainerLight,
    surfaceContainerHigh = FlixColorTokens.SurfaceContainerHighLight,
    surfaceContainerHighest = FlixColorTokens.SurfaceContainerHighestLight,
)

internal val FlixDarkColorScheme: ColorScheme = darkColorScheme(
    primary = FlixColorTokens.PrimaryDark,
    onPrimary = FlixColorTokens.OnPrimaryDark,
    primaryContainer = FlixColorTokens.PrimaryContainerDark,
    onPrimaryContainer = FlixColorTokens.OnPrimaryContainerDark,
    inversePrimary = FlixColorTokens.InversePrimaryDark,
    secondary = FlixColorTokens.SecondaryDark,
    onSecondary = FlixColorTokens.OnSecondaryDark,
    secondaryContainer = FlixColorTokens.SecondaryContainerDark,
    onSecondaryContainer = FlixColorTokens.OnSecondaryContainerDark,
    tertiary = FlixColorTokens.TertiaryDark,
    onTertiary = FlixColorTokens.OnTertiaryDark,
    tertiaryContainer = FlixColorTokens.TertiaryContainerDark,
    onTertiaryContainer = FlixColorTokens.OnTertiaryContainerDark,
    background = FlixColorTokens.BackgroundDark,
    onBackground = FlixColorTokens.OnBackgroundDark,
    surface = FlixColorTokens.SurfaceDark,
    onSurface = FlixColorTokens.OnSurfaceDark,
    surfaceVariant = FlixColorTokens.SurfaceVariantDark,
    onSurfaceVariant = FlixColorTokens.OnSurfaceVariantDark,
    outline = FlixColorTokens.OutlineDark,
    outlineVariant = FlixColorTokens.OutlineVariantDark,
    inverseSurface = FlixColorTokens.InverseSurfaceDark,
    inverseOnSurface = FlixColorTokens.InverseOnSurfaceDark,
    surfaceDim = FlixColorTokens.SurfaceDimDark,
    surfaceBright = FlixColorTokens.SurfaceBrightDark,
    surfaceContainerLowest = FlixColorTokens.SurfaceContainerLowestDark,
    surfaceContainerLow = FlixColorTokens.SurfaceContainerLowDark,
    surfaceContainer = FlixColorTokens.SurfaceContainerDark,
    surfaceContainerHigh = FlixColorTokens.SurfaceContainerHighDark,
    surfaceContainerHighest = FlixColorTokens.SurfaceContainerHighestDark,
)
