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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class RecipeListViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    // Test data
    private val now = Clock.System.now()
    private val testRecipes = listOf(
        Recipe(
            id = 1L,
            name = "Mojito",
            altName = "Classic Mojito",
            about = "A refreshing Cuban cocktail",
            alcoholic = true,
            glassware = "Highball glass",
            garnish = "Mint sprig",
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
            about = "Non-alcoholic tropical delight",
            alcoholic = false,
            glassware = "Hurricane glass",
            garnish = "Pineapple wedge",
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

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testNavigationEventFlow() = testScope.runTest {
        // Setup
        val getRecipesUseCase = object : GetRecipesUseCase {
            override fun invoke(): Flow<List<Recipe>> = flowOf(emptyList())
        }
        val viewModel = RecipeListViewModel(
            getRecipesUseCase,
            FakeSearchRecipesUseCase(),
            FakeDeleteRecipeUseCase(),
            FakeToggleFavoriteRecipeUseCase(),
            FakeUpdateSortOptionUseCase(),
            FakeUpdateFilterOptionUseCase(),
            FakeUpdateViewModeUseCase()
        )

        // Initially null
        assertEquals(null, viewModel.navigationEvent.value)

        // Select a recipe should update navigation event
        viewModel.onEvent(RecipeListUiEvent.SelectRecipe(1L))
        assertEquals(1L, viewModel.navigationEvent.value)

        // Consuming should reset to null
        viewModel.consumeNavigationEvent()
        assertEquals(null, viewModel.navigationEvent.value)
    }

    @Test
    fun testToggleFavoriteEvent() = testScope.runTest {
        // Setup
        val getRecipesUseCase = object : GetRecipesUseCase {
            override fun invoke(): Flow<List<Recipe>> = flowOf(emptyList())
        }
        val toggleFavoriteUseCase = FakeToggleFavoriteRecipeUseCase()
        val viewModel = RecipeListViewModel(
            getRecipesUseCase,
            FakeSearchRecipesUseCase(),
            FakeDeleteRecipeUseCase(),
            toggleFavoriteUseCase,
            FakeUpdateSortOptionUseCase(),
            FakeUpdateFilterOptionUseCase(),
            FakeUpdateViewModeUseCase()
        )

        // Act
        viewModel.onEvent(RecipeListUiEvent.ToggleFavorite(1L))

        // Need to complete all coroutines since this is a suspend function
        advanceUntilIdle()

        // Assert
        assertTrue(toggleFavoriteUseCase.toggledIds.contains(1L))
    }

    @Test
    fun testDeleteRecipeEvent() = testScope.runTest {
        // Setup
        val getRecipesUseCase = object : GetRecipesUseCase {
            override fun invoke(): Flow<List<Recipe>> = flowOf(emptyList())
        }
        val deleteRecipeUseCase = FakeDeleteRecipeUseCase()
        val viewModel = RecipeListViewModel(
            getRecipesUseCase,
            FakeSearchRecipesUseCase(),
            deleteRecipeUseCase,
            FakeToggleFavoriteRecipeUseCase(),
            FakeUpdateSortOptionUseCase(),
            FakeUpdateFilterOptionUseCase(),
            FakeUpdateViewModeUseCase()
        )

        // Act
        viewModel.onEvent(RecipeListUiEvent.DeleteRecipe(2L))

        // Need to complete all coroutines since this is a suspend function
        advanceUntilIdle()

        // Assert
        assertTrue(deleteRecipeUseCase.deletedIds.contains(2L))
    }

    @Test
    fun testUpdateSortOptionEvent() = testScope.runTest {
        // Setup
        val getRecipesUseCase = object : GetRecipesUseCase {
            override fun invoke(): Flow<List<Recipe>> = flowOf(emptyList())
        }
        val updateSortOptionUseCase = FakeUpdateSortOptionUseCase()
        val viewModel = RecipeListViewModel(
            getRecipesUseCase,
            FakeSearchRecipesUseCase(),
            FakeDeleteRecipeUseCase(),
            FakeToggleFavoriteRecipeUseCase(),
            updateSortOptionUseCase,
            FakeUpdateFilterOptionUseCase(),
            FakeUpdateViewModeUseCase()
        )

        // Act
        viewModel.onEvent(RecipeListUiEvent.UpdateSortOption(SortOption.NAME_DESC))

        // Need to complete all coroutines since this is a suspend function
        advanceUntilIdle()

        // Assert
        assertEquals(SortOption.NAME_DESC, updateSortOptionUseCase.lastSortOption)
    }

    @Test
    fun testUpdateFilterOptionEvent() = testScope.runTest {
        // Setup
        val getRecipesUseCase = object : GetRecipesUseCase {
            override fun invoke(): Flow<List<Recipe>> = flowOf(emptyList())
        }
        val updateFilterOptionUseCase = FakeUpdateFilterOptionUseCase()
        val viewModel = RecipeListViewModel(
            getRecipesUseCase,
            FakeSearchRecipesUseCase(),
            FakeDeleteRecipeUseCase(),
            FakeToggleFavoriteRecipeUseCase(),
            FakeUpdateSortOptionUseCase(),
            updateFilterOptionUseCase,
            FakeUpdateViewModeUseCase()
        )

        // Act
        viewModel.onEvent(RecipeListUiEvent.UpdateFilterOption(FilterOption.FAVORITES))

        // Need to complete all coroutines since this is a suspend function
        advanceUntilIdle()

        // Assert
        assertEquals(FilterOption.FAVORITES, updateFilterOptionUseCase.lastFilterOption)
    }

    @Test
    fun testUpdateViewModeEvent() = testScope.runTest {
        // Setup
        val getRecipesUseCase = object : GetRecipesUseCase {
            override fun invoke(): Flow<List<Recipe>> = flowOf(emptyList())
        }
        val updateViewModeUseCase = FakeUpdateViewModeUseCase()
        val viewModel = RecipeListViewModel(
            getRecipesUseCase,
            FakeSearchRecipesUseCase(),
            FakeDeleteRecipeUseCase(),
            FakeToggleFavoriteRecipeUseCase(),
            FakeUpdateSortOptionUseCase(),
            FakeUpdateFilterOptionUseCase(),
            updateViewModeUseCase
        )

        // Act
        viewModel.onEvent(RecipeListUiEvent.UpdateViewMode(ViewMode.GRID))

        // Need to complete all coroutines since this is a suspend function
        advanceUntilIdle()

        // Assert
        assertEquals(ViewMode.GRID, updateViewModeUseCase.lastViewMode)
    }

    @Test
    fun testSearchQueryEvent() = testScope.runTest {
        // Setup
        val getRecipesUseCase = object : GetRecipesUseCase {
            override fun invoke(): Flow<List<Recipe>> = flowOf(emptyList())
        }
        val searchRecipesUseCase = FakeSearchRecipesUseCase()
        val viewModel = RecipeListViewModel(
            getRecipesUseCase,
            searchRecipesUseCase,
            FakeDeleteRecipeUseCase(),
            FakeToggleFavoriteRecipeUseCase(),
            FakeUpdateSortOptionUseCase(),
            FakeUpdateFilterOptionUseCase(),
            FakeUpdateViewModeUseCase()
        )

        // Act
        viewModel.onEvent(RecipeListUiEvent.UpdateSearchQuery("mojito"))

        // Need to complete all coroutines
        advanceUntilIdle()

        // Assert
        assertEquals("mojito", searchRecipesUseCase.lastQuery)
    }
}

// Test implementations
class FakeSearchRecipesUseCase : SearchRecipesUseCase {
    var lastQuery = ""
    override fun invoke(query: String): Flow<List<Recipe>> {
        lastQuery = query
        return flowOf(emptyList())
    }
}

class FakeDeleteRecipeUseCase : DeleteRecipeUseCase {
    val deletedIds = mutableListOf<Long>()
    override suspend fun invoke(id: Long): Boolean {
        deletedIds.add(id)
        return true
    }
}

class FakeToggleFavoriteRecipeUseCase : ToggleFavoriteRecipeUseCase {
    val toggledIds = mutableListOf<Long>()
    override suspend fun invoke(id: Long): Boolean {
        toggledIds.add(id)
        return true
    }
}

class FakeUpdateSortOptionUseCase : UpdateSortOptionUseCase {
    var lastSortOption: SortOption? = null
    override suspend fun invoke(sortOption: SortOption) {
        lastSortOption = sortOption
    }
}

class FakeUpdateFilterOptionUseCase : UpdateFilterOptionUseCase {
    var lastFilterOption: FilterOption? = null
    override suspend fun invoke(filterOption: FilterOption) {
        lastFilterOption = filterOption
    }
}

class FakeUpdateViewModeUseCase : UpdateViewModeUseCase {
    var lastViewMode: ViewMode? = null
    override suspend fun invoke(viewMode: ViewMode) {
        lastViewMode = viewMode
    }
}