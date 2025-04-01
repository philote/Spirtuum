package com.josephhopson.sprituum.ui.recipedetail

import com.josephhopson.sprituum.domain.model.Amount
import com.josephhopson.sprituum.domain.model.Ingredient
import com.josephhopson.sprituum.domain.model.Instruction
import com.josephhopson.sprituum.domain.model.Recipe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for RecipeDetailScreen's navigation and event handling
 */
@ExperimentalCoroutinesApi
class RecipeDetailScreenTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val uiState = MutableStateFlow(RecipeDetailUiState())
    private val navigationEvent = MutableStateFlow<RecipeDetailNavigationEvent?>(null)
    private val now = Clock.System.now()

    private val testRecipe = Recipe(
        id = 1L,
        name = "Mojito",
        altName = "Cuban Mojito",
        favorite = false,
        about = "A traditional Cuban highball.",
        tags = listOf("refreshing", "summer", "mint"),
        instructions = listOf(
            Instruction(1, "Muddle mint leaves with sugar and lime juice."),
            Instruction(2, "Add rum and fill with soda water."),
            Instruction(3, "Garnish with mint leaves and lime wedge.")
        ),
        notes = "Best served very cold with crushed ice.",
        alcoholic = true,
        glassware = "Highball glass",
        garnish = "Mint sprig and lime wedge",
        ingredients = listOf(
            Ingredient(
                name = "White rum",
                amount = Amount(2.0, "oz")
            ),
            Ingredient(
                name = "Fresh lime juice",
                amount = Amount(1.0, "oz")
            ),
            Ingredient(
                name = "Sugar",
                amount = Amount(2.0, "tsp")
            ),
            Ingredient(
                name = "Mint leaves",
                amount = Amount(6.0, "leaves"),
                notes = "Plus extra for garnish"
            ),
            Ingredient(
                name = "Soda water",
                amount = Amount(3.0, "oz")
            )
        ),
        createdAt = now,
        updatedAt = now
    )

    @Test
    fun testNavigationEventFlow() = testScope.runTest {
        val viewModel = TestRecipeDetailViewModel(uiState, navigationEvent)

        // Initially null
        assertNull(viewModel.navigationEvent.value)

        // Navigate back should create NavigateBack event
        viewModel.onEvent(RecipeDetailUiEvent.NavigateBack)
        assertEquals(RecipeDetailNavigationEvent.NavigateBack, viewModel.navigationEvent.value)

        // Consuming should reset to null
        viewModel.consumeNavigationEvent()
        assertNull(viewModel.navigationEvent.value)
    }

    @Test
    fun testEditRecipeNavigationEvent() = testScope.runTest {
        val viewModel = TestRecipeDetailViewModel(uiState, navigationEvent)

        viewModel.onEvent(RecipeDetailUiEvent.EditRecipe)

        // Should create NavigateToEdit event with the recipe ID
        assertTrue(viewModel.navigationEvent.value is RecipeDetailNavigationEvent.NavigateToEdit)
        assertEquals(
            1L,
            (viewModel.navigationEvent.value as RecipeDetailNavigationEvent.NavigateToEdit).recipeId
        )
    }

    @Test
    fun testShareRecipeEvent() = testScope.runTest {
        // Setup with recipe in the UI state
        val stateWithRecipe = MutableStateFlow(
            RecipeDetailUiState(recipe = testRecipe)
        )
        val viewModel = TestRecipeDetailViewModel(stateWithRecipe, navigationEvent)

        viewModel.onEvent(RecipeDetailUiEvent.ShareRecipe)

        // Should create ShareRecipe event with the recipe
        assertTrue(viewModel.navigationEvent.value is RecipeDetailNavigationEvent.ShareRecipe)
        assertEquals(
            testRecipe,
            (viewModel.navigationEvent.value as RecipeDetailNavigationEvent.ShareRecipe).recipe
        )
    }

    @Test
    fun testToggleFavoriteEvent() = testScope.runTest {
        val viewModel = TestRecipeDetailViewModel(uiState, navigationEvent)

        viewModel.onEvent(RecipeDetailUiEvent.ToggleFavorite)

        assertEquals(RecipeDetailUiEvent.ToggleFavorite, viewModel.lastEvent)
    }

    @Test
    fun testDeleteRecipeEvent() = testScope.runTest {
        val viewModel = TestRecipeDetailViewModel(uiState, navigationEvent)

        viewModel.onEvent(RecipeDetailUiEvent.DeleteRecipe)

        assertEquals(RecipeDetailUiEvent.DeleteRecipe, viewModel.lastEvent)
    }

    // Simple implementation for testing event capturing
    private class TestRecipeDetailViewModel(
        private val testUiState: MutableStateFlow<RecipeDetailUiState>,
        private val testNavigationEvent: MutableStateFlow<RecipeDetailNavigationEvent?>
    ) {
        var lastEvent: RecipeDetailUiEvent? = null

        val uiState: StateFlow<RecipeDetailUiState>
            get() = testUiState

        val navigationEvent: StateFlow<RecipeDetailNavigationEvent?>
            get() = testNavigationEvent

        fun onEvent(event: RecipeDetailUiEvent) {
            lastEvent = event
            when (event) {
                RecipeDetailUiEvent.NavigateBack ->
                    testNavigationEvent.value = RecipeDetailNavigationEvent.NavigateBack

                RecipeDetailUiEvent.EditRecipe ->
                    testNavigationEvent.value = RecipeDetailNavigationEvent.NavigateToEdit(1L)

                RecipeDetailUiEvent.ShareRecipe ->
                    testUiState.value.recipe?.let { recipe ->
                        testNavigationEvent.value = RecipeDetailNavigationEvent.ShareRecipe(recipe)
                    }

                else -> { /* No-op */
                }
            }
        }

        fun consumeNavigationEvent() {
            testNavigationEvent.value = null
        }
    }
}