package com.josephhopson.sprituum.ui.components

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.josephhopson.sprituum.theme.AppTheme
import com.josephhopson.sprituum.ui.test.ComposeUiTest
import org.junit.Test
import kotlin.test.assertTrue

class FilterChipTest : ComposeUiTest() {

    @Test
    fun test_filter_chip_displays_text() {
        // When
        setContent {
            AppTheme {
                FilterChip(
                    text = "All Recipes",
                    selected = false,
                    onClick = { }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("All Recipes").assertExists()
    }

    @Test
    fun test_filter_chip_click_triggers_callback() {
        // Given
        var clicked = false

        // When
        setContent {
            AppTheme {
                FilterChip(
                    text = "Favorites",
                    selected = false,
                    onClick = { clicked = true }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Favorites filter. Not selected.")
            .assertHasClickAction()
            .performClick()

        assertTrue(clicked)
    }

    @Test
    fun test_filter_chip_selected_state() {
        // Given - selected state
        setContent {
            AppTheme {
                FilterChip(
                    text = "Alcoholic",
                    selected = true,
                    onClick = { }
                )
            }
        }

        // Then - should have selected state in content description
        composeTestRule.onNodeWithContentDescription("Alcoholic filter. Selected.")
            .assertExists()
            .assertHasClickAction()
    }

    @Test
    fun test_filter_chip_unselected_state() {
        // Given - unselected state
        setContent {
            AppTheme {
                FilterChip(
                    text = "Alcoholic",
                    selected = false,
                    onClick = { }
                )
            }
        }

        // Then - should have not selected state in content description
        composeTestRule.onNodeWithContentDescription("Alcoholic filter. Not selected.")
            .assertExists()
            .assertHasClickAction()
    }

    @Test
    fun test_chip_toggles_selection_on_click() {
        // Given
        var selected = false

        // When
        setContent {
            AppTheme {
                FilterChip(
                    text = "Non-Alcoholic",
                    selected = selected,
                    onClick = { selected = !selected }
                )
            }
        }

        // Then - initial state is not selected
        composeTestRule.onNodeWithContentDescription("Non-Alcoholic filter. Not selected.")
            .assertExists()
            .performClick()

        // After click - selection should toggle
        assertTrue(selected)
    }
}