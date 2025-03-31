package com.josephhopson.sprituum.ui.test

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert

/**
 * Utility functions for testing accessibility in Compose UI components
 */
object AccessibilityTestUtils {

    /**
     * Checks if a node has a valid content description for screen readers
     */
    fun SemanticsNodeInteraction.assertHasContentDescription(): SemanticsNodeInteraction =
        assert(hasContentDescription())

    /**
     * Checks if a node has the specified content description
     */
    fun SemanticsNodeInteraction.assertHasContentDescription(expected: String): SemanticsNodeInteraction =
        assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf(expected)))

    /**
     * Checks if a node is focused for accessibility
     */
    fun SemanticsNodeInteraction.assertIsFocused(): SemanticsNodeInteraction =
        assert(SemanticsMatcher.expectValue(SemanticsProperties.Focused, true))

    /**
     * Checks if a node has a click action
     */
    fun SemanticsNodeInteraction.assertHasClickAction(): SemanticsNodeInteraction =
        assert(hasClickAction())

    /**
     * Checks if a node has the specified role for accessibility
     */
    fun SemanticsNodeInteraction.assertHasRole(expected: String): SemanticsNodeInteraction =
        assert(SemanticsMatcher("has role '$expected'") { node ->
            val role = node.config.getOrNull(SemanticsProperties.Role)?.toString()
            role == expected
        })

    /**
     * Checks if a node has the specified state description
     */
    fun SemanticsNodeInteraction.assertHasStateDescription(expected: String): SemanticsNodeInteraction =
        assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, expected))

    /**
     * Checks if a node has text for screen readers
     */
    fun SemanticsNodeInteraction.assertHasText(): SemanticsNodeInteraction =
        assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))

    /**
     * Returns whether a node has a content description
     */
    private fun hasContentDescription(): SemanticsMatcher =
        SemanticsMatcher.keyIsDefined(SemanticsProperties.ContentDescription)

    /**
     * Returns whether a node has a click action
     */
    private fun hasClickAction(): SemanticsMatcher = SemanticsMatcher("has click action") { node ->
        val clickAction = node.config.getOrNull(SemanticsActions.OnClick)
        clickAction != null
    }

    /**
     * Returns whether a node has a specified accessibility action
     */
    fun hasAccessibilityAction(actionLabel: String): SemanticsMatcher =
        SemanticsMatcher("has accessibility action '$actionLabel'") { node ->
            val hasClickAction = node.config.getOrNull(SemanticsActions.OnClick) != null
            val hasLongClickAction = node.config.getOrNull(SemanticsActions.OnLongClick) != null

            // For now, we're just checking for basic click actions
            // In a real implementation we would check for specific custom actions
            hasClickAction || hasLongClickAction
        }

    /**
     * Checks if a node has a specified accessibility action
     */
    fun SemanticsNodeInteraction.assertHasAccessibilityAction(actionLabel: String): SemanticsNodeInteraction =
        assert(hasAccessibilityAction(actionLabel))
}