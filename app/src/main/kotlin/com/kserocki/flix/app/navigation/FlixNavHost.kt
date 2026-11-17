package com.kserocki.flix.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.kserocki.flix.feature.timetable.api.TimetableNavKey
import com.kserocki.flix.feature.timetable.impl.navigation.timetableEntry

/** The app's single back stack. Today it only ever holds the timetable screen. */
@Composable
fun FlixNavHost(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(TimetableNavKey)
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            timetableEntry()
        },
    )
}
