package com.josephhopson.sprituum.ui.recipelist

import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.FilterOption
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.SortOption
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.ViewMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RecipeListScreenTest {
    private val uiState = MutableStateFlow(RecipeListUiState())
    private val navigationEvent = MutableStateFlow<Long?>(null)
    private val now = Clock.System.now()

    private val testRecipes = listOf(
        Recipe(
            id = 1L,
            name = "Mojito",
            altName = null,
            about = null,
            alcoholic = true,
            glassware = null,
            garnish = null,
            favorite = false,
            tags = listOf("Refreshing", "Summer"),
            ingredients = emptyList(),
            instructions = emptyList(),
            notes = null,
            imagePath = null,
            createdAt = now,
            updatedAt = now
        ),
        Recipe(
            id = 2L,
            name = "Virgin Colada",
            altName = null,
            about = null,
            alcoholic = false,
            glassware = null,
            garnish = null,
            favorite = true,
            tags = listOf("Sweet", "Tropical"),
            ingredients = emptyList(),
            instructions = emptyList(),
            notes = null,
            imagePath = null,
            createdAt = now,
            updatedAt = now
        )
    )

    @Test
    fun testNavigationEventFlow() {
        val viewModel = TestRecipeListViewModel(uiState, navigationEvent)

        // Initially null
        assertNull(viewModel.navigationEvent.value)

        // Select a recipe should update navigation event
        viewModel.onEvent(RecipeListUiEvent.SelectRecipe(1L))
        assertEquals(1L, viewModel.navigationEvent.value)

        // Consuming should reset to null
        viewModel.consumeNavigationEvent()
        assertNull(viewModel.navigationEvent.value)
    }

    @Test
    fun testToggleFavoriteEvent() {
        val viewModel = TestRecipeListViewModel(uiState, navigationEvent)

        viewModel.onEvent(RecipeListUiEvent.ToggleFavorite(1L))

        assertEquals(RecipeListUiEvent.ToggleFavorite(1L), viewModel.lastEvent)
    }

    @Test
    fun testUpdateSortOptionEvent() {
        val viewModel = TestRecipeListViewModel(uiState, navigationEvent)

        viewModel.onEvent(RecipeListUiEvent.UpdateSortOption(SortOption.NAME_DESC))

        assertEquals(RecipeListUiEvent.UpdateSortOption(SortOption.NAME_DESC), viewModel.lastEvent)
    }

    @Test
    fun testUpdateFilterOptionEvent() {
        val viewModel = TestRecipeListViewModel(uiState, navigationEvent)

        viewModel.onEvent(RecipeListUiEvent.UpdateFilterOption(FilterOption.FAVORITES))

        assertEquals(RecipeListUiEvent.UpdateFilterOption(FilterOption.FAVORITES), viewModel.lastEvent)
    }

    @Test
    fun testUpdateViewModeEvent() {
        val viewModel = TestRecipeListViewModel(uiState, navigationEvent)

        viewModel.onEvent(RecipeListUiEvent.UpdateViewMode(ViewMode.GRID))

        assertEquals(RecipeListUiEvent.UpdateViewMode(ViewMode.GRID), viewModel.lastEvent)
    }

    @Test
    fun testUpdateSearchQueryEvent() {
        val viewModel = TestRecipeListViewModel(uiState, navigationEvent)

        viewModel.onEvent(RecipeListUiEvent.UpdateSearchQuery("mojito"))

        assertEquals(RecipeListUiEvent.UpdateSearchQuery("mojito"), viewModel.lastEvent)
    }

    @Test
    fun testClearSearchEvent() {
        val viewModel = TestRecipeListViewModel(uiState, navigationEvent)

        viewModel.onEvent(RecipeListUiEvent.ClearSearch)

        assertEquals(RecipeListUiEvent.ClearSearch, viewModel.lastEvent)
    }

    // Simple implementation for testing event capturing
    private class TestRecipeListViewModel(
        private val testUiState: MutableStateFlow<RecipeListUiState>,
        private val testNavigationEvent: MutableStateFlow<Long?>
    ) {
        var lastEvent: RecipeListUiEvent? = null

        val uiState: StateFlow<RecipeListUiState>
            get() = testUiState

        val navigationEvent: StateFlow<Long?>
            get() = testNavigationEvent

        fun onEvent(event: RecipeListUiEvent) {
            lastEvent = event
            when (event) {
                is RecipeListUiEvent.SelectRecipe -> testNavigationEvent.value = event.recipeId
                is RecipeListUiEvent.CreateNewRecipe -> testNavigationEvent.value = 0L
                else -> { /* No-op */
                }
            }
        }

        fun consumeNavigationEvent() {
            testNavigationEvent.value = null
        }
    }
}