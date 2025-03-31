package com.josephhopson.sprituum.ui.components

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.theme.AppTheme
import com.josephhopson.sprituum.ui.test.ComposeUiTest
import kotlinx.datetime.Clock
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RecipeCardTest : ComposeUiTest() {

    private val mockRecipe = Recipe(
        id = 1,
        name = "Mojito",
        altName = "Cuban Mojito",
        favorite = false,
        about = "A refreshing Cuban cocktail with mint and lime",
        alcoholic = true,
        glassware = "Highball glass",
        tags = listOf("Cuban", "Refreshing", "Summer", "Classic"),
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now()
    )

    @Test
    fun test_recipe_card_displays_all_information() {
        // Given
        var clickCount = 0
        var favoriteToggled = false

        // When
        setContent {
            AppTheme {
                RecipeCard(
                    recipe = mockRecipe,
                    onClick = { clickCount++ },
                    onFavoriteToggle = { favoriteToggled = true }
                )
            }
        }

        // Then
        // Check that all text elements are displayed
        composeTestRule.onNodeWithText("Mojito").assertExists()
        composeTestRule.onNodeWithText("Cuban Mojito").assertExists()
        composeTestRule.onNodeWithText("Alcoholic").assertExists()
        composeTestRule.onNodeWithText("Highball glass").assertExists()
        composeTestRule.onNodeWithText("A refreshing Cuban cocktail with mint and lime").assertExists()

        // Check tags
        composeTestRule.onNodeWithText("#Cuban").assertExists()
        composeTestRule.onNodeWithText("#Refreshing").assertExists()
        composeTestRule.onNodeWithText("#Summer").assertExists()
        composeTestRule.onNodeWithText("+1").assertExists() // For the extra tag

        // Check accessibility
        composeTestRule.onNodeWithContentDescription("Recipe card for Mojito").assertExists()
        composeTestRule.onNodeWithContentDescription("Add Mojito to favorites").assertExists()
    }

    @Test
    fun test_recipe_card_click_triggers_callback() {
        // Given
        var clickCount = 0

        // When
        setContent {
            AppTheme {
                RecipeCard(
                    recipe = mockRecipe,
                    onClick = { clickCount++ },
                    onFavoriteToggle = { }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Recipe card for Mojito")
            .assertHasClickAction()
            .performClick()

        assertEquals(1, clickCount)
    }

    @Test
    fun test_favorite_button_click_triggers_callback() {
        // Given
        var favoriteToggled = false

        // When
        setContent {
            AppTheme {
                RecipeCard(
                    recipe = mockRecipe,
                    onClick = { },
                    onFavoriteToggle = { favoriteToggled = true }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Add Mojito to favorites")
            .assertHasClickAction()
            .performClick()

        assertTrue(favoriteToggled)
    }

    @Test
    fun test_favorite_button_shows_correct_icon_based_on_state() {
        // Given a favorited recipe
        val favoritedRecipe = mockRecipe.copy(favorite = true)

        // When
        setContent {
            AppTheme {
                RecipeCard(
                    recipe = favoritedRecipe,
                    onClick = { },
                    onFavoriteToggle = { }
                )
            }
        }

        // Then the favorite button should indicate removal
        composeTestRule.onNodeWithContentDescription("Remove Mojito from favorites")
            .assertExists()
            .assertHasClickAction()
    }
}