package com.josephhopson.sprituum.ui.recipelist

import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.FilterOption
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.SortOption
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.ViewMode
import com.josephhopson.sprituum.domain.usecase.DeleteRecipeUseCase
import com.josephhopson.sprituum.domain.usecase.GetRecipesUseCase
import com.josephhopson.sprituum.domain.usecase.SearchRecipesUseCase
import com.josephhopson.sprituum.domain.usecase.ToggleFavoriteRecipeUseCase
import com.josephhopson.sprituum.domain.usecase.UpdateFilterOptionUseCase
import com.josephhopson.sprituum.domain.usecase.UpdateSortOptionUseCase
import com.josephhopson.sprituum.domain.usecase.UpdateViewModeUseCase
import com.josephhopson.sprituum.ui.test.ViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class RecipeListViewModelTest : ViewModelTest() {

    private lateinit var viewModel: RecipeListViewModel
    private lateinit var fakeGetRecipesUseCase: FakeGetRecipesUseCase
    private lateinit var fakeSearchRecipesUseCase: FakeSearchRecipesUseCase
    private lateinit var fakeDeleteRecipeUseCase: FakeDeleteRecipeUseCase
    private lateinit var fakeToggleFavoriteRecipeUseCase: FakeToggleFavoriteRecipeUseCase
    private lateinit var fakeUpdateSortOptionUseCase: FakeUpdateSortOptionUseCase
    private lateinit var fakeUpdateFilterOptionUseCase: FakeUpdateFilterOptionUseCase
    private lateinit var fakeUpdateViewModeUseCase: FakeUpdateViewModeUseCase

    // Test data
    private val testRecipes = listOf(
        Recipe(
            id = 1,
            name = "Mojito",
            alcoholic = true,
            favorite = false,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        ),
        Recipe(
            id = 2,
            name = "Virgin Colada",
            alcoholic = false,
            favorite = true,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        ),
        Recipe(
            id = 3,
            name = "Margarita",
            alcoholic = true,
            favorite = false,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    )

    override fun setupTest() {
        fakeGetRecipesUseCase = FakeGetRecipesUseCase(testRecipes)
        fakeSearchRecipesUseCase = FakeSearchRecipesUseCase(testRecipes)
        fakeDeleteRecipeUseCase = FakeDeleteRecipeUseCase()
        fakeToggleFavoriteRecipeUseCase = FakeToggleFavoriteRecipeUseCase()
        fakeUpdateSortOptionUseCase = FakeUpdateSortOptionUseCase()
        fakeUpdateFilterOptionUseCase = FakeUpdateFilterOptionUseCase()
        fakeUpdateViewModeUseCase = FakeUpdateViewModeUseCase()

        viewModel = RecipeListViewModel(
            getRecipesUseCase = fakeGetRecipesUseCase,
            searchRecipesUseCase = fakeSearchRecipesUseCase,
            deleteRecipeUseCase = fakeDeleteRecipeUseCase,
            toggleFavoriteRecipeUseCase = fakeToggleFavoriteRecipeUseCase,
            updateSortOptionUseCase = fakeUpdateSortOptionUseCase,
            updateFilterOptionUseCase = fakeUpdateFilterOptionUseCase,
            updateViewModeUseCase = fakeUpdateViewModeUseCase
        )
    }

    @Test
    fun test_initial_state_loads_recipes() = runTest {
        // Check initial state
        val initialState = viewModel.uiState.value

        // Verify that recipes are loaded
        assertEquals(testRecipes, initialState.recipes)
        assertFalse(initialState.isLoading)
        assertNull(initialState.error)
    }

    @Test
    fun test_delete_recipe_success() = runTest {
        // Given
        fakeDeleteRecipeUseCase.shouldSucceed = true

        // When
        viewModel.onEvent(RecipeListUiEvent.DeleteRecipe(1))

        // Then
        assertTrue(fakeDeleteRecipeUseCase.wasInvoked)
        assertEquals(1L, fakeDeleteRecipeUseCase.lastRecipeId)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun test_delete_recipe_failure() = runTest {
        // Given
        fakeDeleteRecipeUseCase.shouldSucceed = false

        // When
        viewModel.onEvent(RecipeListUiEvent.DeleteRecipe(1))

        // Then
        assertTrue(fakeDeleteRecipeUseCase.wasInvoked)
        assertEquals(1L, fakeDeleteRecipeUseCase.lastRecipeId)
        assertEquals("Failed to delete recipe", viewModel.uiState.value.error)
    }

    @Test
    fun test_toggle_favorite_success() = runTest {
        // Given
        fakeToggleFavoriteRecipeUseCase.shouldSucceed = true

        // When
        viewModel.onEvent(RecipeListUiEvent.ToggleFavorite(1))

        // Then
        assertTrue(fakeToggleFavoriteRecipeUseCase.wasInvoked)
        assertEquals(1L, fakeToggleFavoriteRecipeUseCase.lastRecipeId)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun test_toggle_favorite_failure() = runTest {
        // Given
        fakeToggleFavoriteRecipeUseCase.shouldSucceed = false

        // When
        viewModel.onEvent(RecipeListUiEvent.ToggleFavorite(1))

        // Then
        assertTrue(fakeToggleFavoriteRecipeUseCase.wasInvoked)
        assertEquals(1L, fakeToggleFavoriteRecipeUseCase.lastRecipeId)
        assertEquals("Failed to update favorite status", viewModel.uiState.value.error)
    }

    @Test
    fun test_select_recipe_updates_navigation_event() = runTest {
        // When
        viewModel.onEvent(RecipeListUiEvent.SelectRecipe(2))

        // Then
        assertEquals(2L, viewModel.navigationEvent.value)

        // When navigation is consumed
        viewModel.consumeNavigationEvent()

        // Then navigation event should be null
        assertNull(viewModel.navigationEvent.value)
    }

    @Test
    fun test_create_new_recipe_updates_navigation_event() = runTest {
        // When
        viewModel.onEvent(RecipeListUiEvent.CreateNewRecipe)

        // Then
        assertEquals(0L, viewModel.navigationEvent.value)
    }

    @Test
    fun test_update_sort_option() = runTest {
        // When
        viewModel.onEvent(RecipeListUiEvent.UpdateSortOption(SortOption.DATE_CREATED_NEWEST))

        // Then
        assertTrue(fakeUpdateSortOptionUseCase.wasInvoked)
        assertEquals(SortOption.DATE_CREATED_NEWEST, fakeUpdateSortOptionUseCase.lastSortOption)
        assertEquals(SortOption.DATE_CREATED_NEWEST, viewModel.uiState.value.sortOption)
    }

    @Test
    fun test_update_filter_option() = runTest {
        // When
        viewModel.onEvent(RecipeListUiEvent.UpdateFilterOption(FilterOption.ALCOHOLIC))

        // Then
        assertTrue(fakeUpdateFilterOptionUseCase.wasInvoked)
        assertEquals(FilterOption.ALCOHOLIC, fakeUpdateFilterOptionUseCase.lastFilterOption)
        assertEquals(FilterOption.ALCOHOLIC, viewModel.uiState.value.filterOption)
    }

    @Test
    fun test_update_view_mode() = runTest {
        // When
        viewModel.onEvent(RecipeListUiEvent.UpdateViewMode(ViewMode.GRID))

        // Then
        assertTrue(fakeUpdateViewModeUseCase.wasInvoked)
        assertEquals(ViewMode.GRID, fakeUpdateViewModeUseCase.lastViewMode)
        assertEquals(ViewMode.GRID, viewModel.uiState.value.viewMode)
    }

    @Test
    fun test_update_search_query() = runTest {
        // Initial state
        assertEquals("", viewModel.uiState.value.searchQuery)

        // When
        viewModel.onEvent(RecipeListUiEvent.UpdateSearchQuery("mojito"))

        // Then
        assertEquals("mojito", viewModel.uiState.value.searchQuery)
        assertTrue(fakeSearchRecipesUseCase.wasInvoked)
        assertEquals("mojito", fakeSearchRecipesUseCase.lastQuery)
    }

    @Test
    fun test_clear_search() = runTest {
        // Given
        viewModel.onEvent(RecipeListUiEvent.UpdateSearchQuery("mojito"))
        assertEquals("mojito", viewModel.uiState.value.searchQuery)

        // When
        viewModel.onEvent(RecipeListUiEvent.ClearSearch)

        // Then
        assertEquals("", viewModel.uiState.value.searchQuery)
    }

    // Fake use case implementations for testing
    private class FakeGetRecipesUseCase(private val recipes: List<Recipe>) : GetRecipesUseCase {
        override fun invoke(): Flow<List<Recipe>> = flowOf(recipes)
    }

    private class FakeSearchRecipesUseCase(private val recipes: List<Recipe>) : SearchRecipesUseCase {
        var wasInvoked = false
        var lastQuery = ""

        override fun invoke(query: String): Flow<List<Recipe>> {
            wasInvoked = true
            lastQuery = query
            return flowOf(
                recipes.filter {
                    it.name.contains(query, ignoreCase = true)
                }
            )
        }
    }

    private class FakeDeleteRecipeUseCase : DeleteRecipeUseCase {
        var wasInvoked = false
        var shouldSucceed = true
        var lastRecipeId = 0L

        override suspend fun invoke(id: Long): Boolean {
            wasInvoked = true
            lastRecipeId = id
            return shouldSucceed
        }
    }

    private class FakeToggleFavoriteRecipeUseCase : ToggleFavoriteRecipeUseCase {
        var wasInvoked = false
        var shouldSucceed = true
        var lastRecipeId = 0L

        override suspend fun invoke(id: Long): Boolean {
            wasInvoked = true
            lastRecipeId = id
            return shouldSucceed
        }
    }

    private class FakeUpdateSortOptionUseCase : UpdateSortOptionUseCase {
        var wasInvoked = false
        var lastSortOption = SortOption.NAME_ASC

        override suspend fun invoke(sortOption: SortOption) {
            wasInvoked = true
            lastSortOption = sortOption
        }
    }

    private class FakeUpdateFilterOptionUseCase : UpdateFilterOptionUseCase {
        var wasInvoked = false
        var lastFilterOption = FilterOption.ALL

        override suspend fun invoke(filterOption: FilterOption) {
            wasInvoked = true
            lastFilterOption = filterOption
        }
    }

    private class FakeUpdateViewModeUseCase : UpdateViewModeUseCase {
        var wasInvoked = false
        var lastViewMode = ViewMode.LIST

        override suspend fun invoke(viewMode: ViewMode) {
            wasInvoked = true
            lastViewMode = viewMode
        }
    }
}