package com.josephhopson.sprituum.ui.recipeedit.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class BasicInfoSectionTest {

    @Test
    fun rendersCorrectly() = runComposeUiTest {
        // Set up initial state
        setContent {
            BasicInfoSection(
                name = "Mojito",
                onNameChange = {},
                nameError = null,
                altName = "Cuban Mojito",
                onAltNameChange = {},
                glassware = "Highball",
                onGlasswareChange = {},
                garnish = "Mint sprig",
                onGarnishChange = {},
                alcoholic = true,
                onAlcoholicToggle = {},
                favorite = false,
                onFavoriteToggle = {}
            )
        }

        // Verify fields display correctly
        onNodeWithContentDescription("Recipe name field").assertExists()
        onNodeWithText("Mojito").assertExists()

        onNodeWithContentDescription("Alternate name field").assertExists()
        onNodeWithText("Cuban Mojito").assertExists()

        onNodeWithContentDescription("Glassware field").assertExists()
        onNodeWithText("Highball").assertExists()

        onNodeWithContentDescription("Garnish field").assertExists()
        onNodeWithText("Mint sprig").assertExists()

        onNodeWithContentDescription("Alcoholic toggle").assertExists()
        onNodeWithContentDescription("Favorite toggle").assertExists()
    }

    @Test
    fun showsValidationError() = runComposeUiTest {
        // Set up with error
        setContent {
            BasicInfoSection(
                name = "",
                onNameChange = {},
                nameError = "Recipe name is required",
                altName = "",
                onAltNameChange = {},
                glassware = "",
                onGlasswareChange = {},
                garnish = "",
                onGarnishChange = {},
                alcoholic = true,
                onAlcoholicToggle = {},
                favorite = false,
                onFavoriteToggle = {}
            )
        }

        // Verify error message is displayed
        onNodeWithText("Recipe name is required").assertExists()
    }

    @Test
    fun handlesTextInput() = runComposeUiTest {
        var nameValue = ""

        // Set up with callbacks
        setContent {
            BasicInfoSection(
                name = nameValue,
                onNameChange = { nameValue = it },
                nameError = null,
                altName = "",
                onAltNameChange = {},
                glassware = "",
                onGlasswareChange = {},
                garnish = "",
                onGarnishChange = {},
                alcoholic = true,
                onAlcoholicToggle = {},
                favorite = false,
                onFavoriteToggle = {}
            )
        }

        // Enter text in the name field
        val testText = "New Recipe"
        onNodeWithContentDescription("Recipe name field").performTextInput(testText)

        // Verify callback was invoked with the correct text
        assertEquals(testText, nameValue)
    }

    @Test
    fun handlesCheckboxToggles() = runComposeUiTest {
        var alcoholicValue = true
        var favoriteValue = false

        // Set up with callbacks
        setContent {
            BasicInfoSection(
                name = "",
                onNameChange = {},
                nameError = null,
                altName = "",
                onAltNameChange = {},
                glassware = "",
                onGlasswareChange = {},
                garnish = "",
                onGarnishChange = {},
                alcoholic = alcoholicValue,
                onAlcoholicToggle = { alcoholicValue = !alcoholicValue },
                favorite = favoriteValue,
                onFavoriteToggle = { favoriteValue = !favoriteValue }
            )
        }

        // Verify initial state
        onNodeWithContentDescription("Alcoholic toggle").assertExists()
        onNodeWithContentDescription("Favorite toggle").assertExists()

        // Toggle favorite checkbox
        onNodeWithContentDescription("Favorite toggle").performClick()

        // Verify callback was invoked
        assertEquals(true, favoriteValue)
    }
}