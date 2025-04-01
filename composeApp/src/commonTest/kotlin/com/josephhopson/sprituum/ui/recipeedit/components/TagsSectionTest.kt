package com.josephhopson.sprituum.ui.recipeedit.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class TagsSectionTest {

    @Test
    fun rendersCorrectly() = runComposeUiTest {
        // Set up with existing tags
        setContent {
            TagsSection(
                tags = listOf("refreshing", "summer", "mint"),
                onDeleteTag = {},
                newTag = "",
                onNewTagChange = {},
                onAddTag = {}
            )
        }

        // Verify tags are displayed
        onNodeWithText("#refreshing").assertExists()
        onNodeWithText("#summer").assertExists()
        onNodeWithText("#mint").assertExists()
    }

    @Test
    fun rendersEmptyState() = runComposeUiTest {
        // Set up with no tags
        setContent {
            TagsSection(
                tags = emptyList(),
                onDeleteTag = {},
                newTag = "",
                onNewTagChange = {},
                onAddTag = {}
            )
        }

        // Verify empty state message is displayed
        onNodeWithText("No tags added").assertExists()
    }

    @Test
    fun handlesTagInput() = runComposeUiTest {
        var tagValue = ""
        var addTagCalled = false

        // Set up with callbacks
        setContent {
            TagsSection(
                tags = emptyList(),
                onDeleteTag = {},
                newTag = tagValue,
                onNewTagChange = { tagValue = it },
                onAddTag = { addTagCalled = true }
            )
        }

        // Enter text in the tag input
        val testTag = "newtag"
        onNodeWithContentDescription("New tag input field").performTextInput(testTag)

        // Verify callback was invoked with the correct text
        assertEquals(testTag, tagValue)

        // Click add button
        onNodeWithContentDescription("Add tag button").performClick()

        // Verify add callback was called
        assertTrue(addTagCalled)
    }

    @Test
    fun handlesTagDeletion() = runComposeUiTest {
        var deletedTagIndex: Int? = null

        // Set up with deletion callback
        setContent {
            TagsSection(
                tags = listOf("tag1", "tag2"),
                onDeleteTag = { deletedTagIndex = it },
                newTag = "",
                onNewTagChange = {},
                onAddTag = {}
            )
        }

        // Delete a tag
        onNodeWithContentDescription("Remove tag tag1").performClick()

        // Verify deletion callback was called with correct index
        assertEquals(0, deletedTagIndex)
    }
}