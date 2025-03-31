package com.josephhopson.sprituum.ui.test

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule

/**
 * Base class for Compose UI tests
 *
 * Provides common functionality for testing Compose UI components
 */
abstract class ComposeUiTest {

    /**
     * Create a compose rule that allows us to set and test composables
     */
    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Sets the content for testing
     *
     * @param content The composable to test
     */
    fun setContent(content: @Composable () -> Unit) {
        composeTestRule.setContent(content)
    }

    /**
     * Waits for all compositions and recompositions to complete
     */
    fun waitForIdle() {
        composeTestRule.waitForIdle()
    }
}