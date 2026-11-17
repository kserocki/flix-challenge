package com.kserocki.flix.feature.timetable.impl.presentation

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performCustomAccessibilityActionWithLabel
import androidx.test.core.app.ApplicationProvider
import com.kserocki.flix.core.designsystem.theme.FlixTheme
import com.kserocki.flix.feature.timetable.impl.R
import kotlinx.collections.immutable.persistentListOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertTrue

/**
 * TalkBack only focuses a node that merges its descendants or a speaking leaf, so the header's
 * refresh action must sit on one merged heading node. uiautomator can't show custom actions, so
 * this checks the real semantics tree on the JVM.
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
class TimetableContentSemanticsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context get() = ApplicationProvider.getApplicationContext()

    private val stationHeader = StationHeaderUiModel(
        stationName = "Berlin central bus station",
        zoneLabel = "Europe/Berlin (UTC+1)",
    )
    private val departure = TimetableListItem.DepartureRow(
        id = "1",
        lineCode = "N160",
        direction = "Trieste",
        departureTime = "16:00",
    )

    @Test
    fun `station header is one merged heading node whose refresh action calls onRefresh`() {
        var refreshed = false
        setContent(
            state = TimetableUiState(
                stationHeader = stationHeader,
                board = TimetableBoard.Departures(persistentListOf(departure)),
            ),
            onIntent = { intent -> if (intent == TimetableIntent.Refresh) refreshed = true },
        )
        val refreshLabel = context.getString(R.string.timetable_refresh_accessibility_action)

        composeTestRule.onNodeWithText(stationHeader.stationName)
            .assert(isHeading())
            .assert(hasText(stationHeader.zoneLabel, substring = true))
            .performCustomAccessibilityActionWithLabel(refreshLabel)

        assertTrue(refreshed)
    }

    @Test
    fun `a departure row is one merged node with the expected content description`() {
        setContent(
            state = TimetableUiState(
                stationHeader = stationHeader,
                board = TimetableBoard.Departures(persistentListOf(departure)),
            ),
            onIntent = {},
        )
        val description = context.getString(
            R.string.timetable_departure_description,
            departure.lineCode,
            departure.direction,
            departure.departureTime,
        )

        composeTestRule.onNodeWithContentDescription(description).assertIsDisplayed()
    }

    @Test
    fun `the loading indicator announces loading departures`() {
        setContent(state = TimetableUiState(board = TimetableBoard.Loading), onIntent = {})
        val description = context.getString(R.string.timetable_loading)

        // mergesDescendants(), not just hasContentDescription(), pins the fix: the Box isn't a
        // leaf (it wraps a CircularProgressIndicator), so without merging it would carry the
        // description but TalkBack would skip it for not being screen-reader-focusable.
        composeTestRule
            .onNodeWithContentDescription(description)
            .assertIsDisplayed()
            .assert(mergesDescendants())
    }

    private fun mergesDescendants(): SemanticsMatcher =
        SemanticsMatcher("merges descendants") { node: SemanticsNode -> node.config.isMergingSemanticsOfDescendants }

    private fun setContent(state: TimetableUiState, onIntent: (TimetableIntent) -> Unit) {
        composeTestRule.setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            FlixTheme {
                TimetableContent(state = state, snackbarHostState = snackbarHostState, onIntent = onIntent)
            }
        }
    }
}
