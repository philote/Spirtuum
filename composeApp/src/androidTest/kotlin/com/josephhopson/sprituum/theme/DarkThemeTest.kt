package com.josephhopson.sprituum.theme

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import com.josephhopson.sprituum.ui.components.FilterChip
import com.josephhopson.sprituum.ui.test.ComposeUiTest
import org.junit.Test

/**
 * Tests for components in dark theme
 */
class DarkThemeTest : ComposeUiTest() {

    @Test
    fun test_filter_chip_dark_theme() {
        // When using dark theme
        setContent {
            AppTheme(darkTheme = true) {
                FilterChip(
                    text = "Dark Theme Test",
                    selected = true,
                    onClick = { }
                )
            }
        }

        // Then the component should still be visible
        composeTestRule.onNodeWithText("Dark Theme Test")
            .assertIsDisplayed()
    }
}