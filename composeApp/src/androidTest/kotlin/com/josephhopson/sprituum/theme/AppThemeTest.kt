package com.josephhopson.sprituum.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

/**
 * Tests for the app theme
 */
class AppThemeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test_light_theme_colors_applied() {
        // When
        composeTestRule.setContent {
            AppTheme(darkTheme = false) {
                TestContent()
            }
        }

        // Wait for composition
        composeTestRule.waitForIdle()

        // Then validate UI is displayed
        composeTestRule.onNodeWithTag("test_surface").assertIsDisplayed()
        composeTestRule.onNodeWithTag("test_text").assertIsDisplayed()

        // Note: In a real test, we could use screenshot comparison to verify colors
        // For now, we're just testing that the theme applies without crashing
    }

    @Test
    fun test_dark_theme_colors_applied() {
        // When
        composeTestRule.setContent {
            AppTheme(darkTheme = true) {
                TestContent()
            }
        }

        // Wait for composition
        composeTestRule.waitForIdle()

        // Then validate UI is displayed
        composeTestRule.onNodeWithTag("test_surface").assertIsDisplayed()
        composeTestRule.onNodeWithTag("test_text").assertIsDisplayed()

        // Note: In a real test, we could use screenshot comparison to verify colors
    }
}

/**
 * Test content that uses theme colors
 */
@Composable
fun TestContent() {
    Surface(
        modifier = Modifier.testTag("test_surface"),
        color = MaterialTheme.colorScheme.surface
    ) {
        Text(
            modifier = Modifier.testTag("test_text"),
            text = "Test Text",
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}