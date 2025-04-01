package com.josephhopson.sprituum.ui.recipedetail

import com.josephhopson.sprituum.domain.model.Amount
import com.josephhopson.sprituum.domain.model.Ingredient
import com.josephhopson.sprituum.domain.model.Instruction
import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.domain.usecase.DeleteRecipeUseCase
import com.josephhopson.sprituum.domain.usecase.GetRecipeByIdUseCase
import com.josephhopson.sprituum.domain.usecase.ToggleFavoriteRecipeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class RecipeDetailViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var viewModel: RecipeDetailViewModel
    private lateinit var getRecipeByIdUseCase: FakeGetRecipeByIdUseCase
    private lateinit var toggleFavoriteRecipeUseCase: FakeToggleFavoriteRecipeUseCase
    private lateinit var deleteRecipeUseCase: FakeDeleteRecipeUseCase

    private val testRecipeId = 1L
    private val testRecipe = Recipe(
        id = testRecipeId,
        name = "Test Recipe",
        alcoholic = true,
        glassware = "Test Glass",
        about = "Test about",
        ingredients = listOf(
            Ingredient(
                name = "Test Ingredient",
                amount = Amount(
                    value = 2.0,
                    label = "oz"
                )
            )
        ),
        instructions = listOf(
            Instruction(
                step = 1,
                value = "Test instruction"
            )
        ),
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now()
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getRecipeByIdUseCase = FakeGetRecipeByIdUseCase().apply {
            returnRecipe = testRecipe
        }
        toggleFavoriteRecipeUseCase = FakeToggleFavoriteRecipeUseCase()
        deleteRecipeUseCase = FakeDeleteRecipeUseCase()

        viewModel = RecipeDetailViewModel(
            recipeId = testRecipeId,
            getRecipeByIdUseCase = getRecipeByIdUseCase,
            toggleFavoriteRecipeUseCase = toggleFavoriteRecipeUseCase,
            deleteRecipeUseCase = deleteRecipeUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadingRecipeByIdSetsUiStateCorrectly() = testScope.runTest {
        // Given viewModel initialized with testRecipeId
        // When (loading happens in init)

        // Then
        assertEquals(testRecipe, viewModel.uiState.value.recipe)
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun loadingNonExistentRecipeShowsError() = testScope.runTest {
        // Given
        getRecipeByIdUseCase.returnRecipe = null

        // When
        viewModel = RecipeDetailViewModel(
            recipeId = 999L,
            getRecipeByIdUseCase = getRecipeByIdUseCase,
            toggleFavoriteRecipeUseCase = toggleFavoriteRecipeUseCase,
            deleteRecipeUseCase = deleteRecipeUseCase
        )

        // Then
        assertNull(viewModel.uiState.value.recipe)
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals("Recipe not found", viewModel.uiState.value.error)
    }

    @Test
    fun toggleFavoriteUpdatesRecipe() = testScope.runTest {
        // Given
        toggleFavoriteRecipeUseCase.returnSuccess = true
        getRecipeByIdUseCase.returnRecipe = testRecipe.copy(favorite = true)

        // When
        viewModel.onEvent(RecipeDetailUiEvent.ToggleFavorite)

        // Make sure all coroutines have run
        advanceUntilIdle()

        // Then
        assertTrue(toggleFavoriteRecipeUseCase.wasToggleCalled)
        assertEquals(testRecipeId, toggleFavoriteRecipeUseCase.lastToggledId)
    }

    @Test
    fun deleteRecipeNavigatesBackOnSuccess() = testScope.runTest {
        // Given
        deleteRecipeUseCase.returnSuccess = true

        // When
        viewModel.onEvent(RecipeDetailUiEvent.DeleteRecipe)

        // Make sure all coroutines have run
        advanceUntilIdle()

        // Then
        assertTrue(deleteRecipeUseCase.wasDeleteCalled)
        assertEquals(testRecipeId, deleteRecipeUseCase.lastDeletedId)
        assertEquals(RecipeDetailNavigationEvent.NavigateBack, viewModel.navigationEvent.value)
    }

    @Test
    fun editRecipeNavigatesToEditScreen() = testScope.runTest {
        // When
        viewModel.onEvent(RecipeDetailUiEvent.EditRecipe)

        // Then
        assertNotNull(viewModel.navigationEvent.value)
        assertTrue(viewModel.navigationEvent.value is RecipeDetailNavigationEvent.NavigateToEdit)
        assertEquals(
            testRecipeId,
            (viewModel.navigationEvent.value as RecipeDetailNavigationEvent.NavigateToEdit).recipeId
        )
    }

    @Test
    fun shareRecipeCreatesShareEvent() = testScope.runTest {
        // When
        viewModel.onEvent(RecipeDetailUiEvent.ShareRecipe)

        // Then
        assertNotNull(viewModel.navigationEvent.value)
        assertTrue(viewModel.navigationEvent.value is RecipeDetailNavigationEvent.ShareRecipe)
        assertEquals(
            testRecipe,
            (viewModel.navigationEvent.value as RecipeDetailNavigationEvent.ShareRecipe).recipe
        )
    }

    @Test
    fun navigateBackCreatesNavigationEvent() = testScope.runTest {
        // When
        viewModel.onEvent(RecipeDetailUiEvent.NavigateBack)

        // Then
        assertEquals(RecipeDetailNavigationEvent.NavigateBack, viewModel.navigationEvent.value)
    }

    @Test
    fun consumeNavigationEventClearsTheEvent() = testScope.runTest {
        // Given
        viewModel.onEvent(RecipeDetailUiEvent.NavigateBack)
        assertNotNull(viewModel.navigationEvent.value)

        // When
        viewModel.consumeNavigationEvent()

        // Then
        assertNull(viewModel.navigationEvent.value)
    }
}

// Test Doubles
class FakeGetRecipeByIdUseCase : GetRecipeByIdUseCase {
    var returnRecipe: Recipe? = null
    var lastRequestedId: Long? = null

    override suspend fun invoke(id: Long): Recipe? {
        lastRequestedId = id
        return returnRecipe
    }
}

class FakeToggleFavoriteRecipeUseCase : ToggleFavoriteRecipeUseCase {
    var returnSuccess = true
    var wasToggleCalled = false
    var lastToggledId: Long? = null

    override suspend fun invoke(id: Long): Boolean {
        wasToggleCalled = true
        lastToggledId = id
        return returnSuccess
    }
}

class FakeDeleteRecipeUseCase : DeleteRecipeUseCase {
    var returnSuccess = true
    var wasDeleteCalled = false
    var lastDeletedId: Long? = null

    override suspend fun invoke(id: Long): Boolean {
        wasDeleteCalled = true
        lastDeletedId = id
        return returnSuccess
    }
}