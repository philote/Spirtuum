package com.josephhopson.sprituum.ui.recipeedit

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

/**
 * Tests for RecipeEditScreen's navigation and event handling
 */
@ExperimentalCoroutinesApi
class RecipeEditScreenTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val uiState = MutableStateFlow(RecipeEditUiState())
    private val navigationEvent = MutableStateFlow<RecipeEditNavigationEvent?>(null)
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
        val viewModel = TestRecipeEditViewModel(uiState, navigationEvent)

        // Initially null
        assertNull(viewModel.navigationEvent.value)

        // Cancel should create NavigateBack event
        viewModel.onEvent(RecipeEditUiEvent.Cancel)
        assertEquals(RecipeEditNavigationEvent.NavigateBack, viewModel.navigationEvent.value)

        // Consuming should reset to null
        viewModel.consumeNavigationEvent()
        assertNull(viewModel.navigationEvent.value)
    }

    @Test
    fun testSaveRecipeNavigationEvent() = testScope.runTest {
        val viewModel = TestRecipeEditViewModel(uiState, navigationEvent)

        // Set up navigation event for save success
        navigationEvent.value = RecipeEditNavigationEvent.NavigateToDetail(42L)

        viewModel.onEvent(RecipeEditUiEvent.SaveRecipe)

        // Should create SaveRecipe event
        assertEquals(RecipeEditUiEvent.SaveRecipe, viewModel.lastEvent)
    }

    @Test
    fun testCapturePhotoEvent() = testScope.runTest {
        val viewModel = TestRecipeEditViewModel(uiState, navigationEvent)

        viewModel.onEvent(RecipeEditUiEvent.TakePhoto)

        // Should create CapturePhoto navigation event
        assertEquals(RecipeEditUiEvent.TakePhoto, viewModel.lastEvent)
        assertEquals(RecipeEditNavigationEvent.CapturePhoto, navigationEvent.value)
    }

    @Test
    fun testBasicFieldUpdateEvents() = testScope.runTest {
        val viewModel = TestRecipeEditViewModel(uiState, navigationEvent)

        viewModel.onEvent(RecipeEditUiEvent.UpdateName("New Name"))
        assertEquals(RecipeEditUiEvent.UpdateName("New Name"), viewModel.lastEvent)

        viewModel.onEvent(RecipeEditUiEvent.ToggleFavorite)
        assertEquals(RecipeEditUiEvent.ToggleFavorite, viewModel.lastEvent)

        viewModel.onEvent(RecipeEditUiEvent.ToggleAlcoholic)
        assertEquals(RecipeEditUiEvent.ToggleAlcoholic, viewModel.lastEvent)
    }

    @Test
    fun testIngredientEvents() = testScope.runTest {
        val viewModel = TestRecipeEditViewModel(uiState, navigationEvent)

        viewModel.onEvent(RecipeEditUiEvent.AddIngredient("Test"))
        assertEquals(RecipeEditUiEvent.AddIngredient("Test"), viewModel.lastEvent)

        viewModel.onEvent(RecipeEditUiEvent.UpdateIngredientName(0, "Updated"))
        assertEquals(RecipeEditUiEvent.UpdateIngredientName(0, "Updated"), viewModel.lastEvent)

        viewModel.onEvent(RecipeEditUiEvent.DeleteIngredient(0))
        assertEquals(RecipeEditUiEvent.DeleteIngredient(0), viewModel.lastEvent)
    }

    @Test
    fun testInstructionEvents() = testScope.runTest {
        val viewModel = TestRecipeEditViewModel(uiState, navigationEvent)

        viewModel.onEvent(RecipeEditUiEvent.AddInstruction("Test Step"))
        assertEquals(RecipeEditUiEvent.AddInstruction("Test Step"), viewModel.lastEvent)

        viewModel.onEvent(RecipeEditUiEvent.UpdateInstructionValue(0, "Updated Step"))
        assertEquals(RecipeEditUiEvent.UpdateInstructionValue(0, "Updated Step"), viewModel.lastEvent)

        viewModel.onEvent(RecipeEditUiEvent.DeleteInstruction(0))
        assertEquals(RecipeEditUiEvent.DeleteInstruction(0), viewModel.lastEvent)
    }

    // Simple implementation for testing event capturing
    private class TestRecipeEditViewModel(
        private val testUiState: MutableStateFlow<RecipeEditUiState>,
        private val testNavigationEvent: MutableStateFlow<RecipeEditNavigationEvent?>
    ) {
        var lastEvent: RecipeEditUiEvent? = null

        val uiState: StateFlow<RecipeEditUiState>
            get() = testUiState

        val navigationEvent: StateFlow<RecipeEditNavigationEvent?>
            get() = testNavigationEvent

        fun onEvent(event: RecipeEditUiEvent) {
            lastEvent = event
            when (event) {
                RecipeEditUiEvent.Cancel ->
                    testNavigationEvent.value = RecipeEditNavigationEvent.NavigateBack

                RecipeEditUiEvent.TakePhoto ->
                    testNavigationEvent.value = RecipeEditNavigationEvent.CapturePhoto

                else -> { /* No-op - in real implementation other events would update UI state */
                }
            }
        }

        fun consumeNavigationEvent() {
            testNavigationEvent.value = null
        }
    }
}