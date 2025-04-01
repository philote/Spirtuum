package com.josephhopson.sprituum.ui.recipeedit

import com.josephhopson.sprituum.domain.model.Amount
import com.josephhopson.sprituum.domain.model.Ingredient
import com.josephhopson.sprituum.domain.model.Instruction
import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.domain.usecase.GetRecipeByIdUseCase
import com.josephhopson.sprituum.domain.usecase.SaveRecipeUseCase
import com.josephhopson.sprituum.ui.test.ViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class RecipeEditViewModelTest : ViewModelTest() {
    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var viewModel: RecipeEditViewModel
    private lateinit var getRecipeByIdUseCase: FakeGetRecipeByIdUseCase
    private lateinit var saveRecipeUseCase: FakeSaveRecipeUseCase

    private val testRecipeId = 1L
    private val now = Clock.System.now()

    private val testRecipe = Recipe(
        id = testRecipeId,
        name = "Test Recipe",
        altName = "Alternative Name",
        favorite = true,
        about = "About text",
        tags = listOf("tag1", "tag2"),
        instructions = listOf(
            Instruction(1, "Step 1 instructions"),
            Instruction(2, "Step 2 instructions")
        ),
        notes = "Test notes",
        alcoholic = true,
        glassware = "Test Glass",
        garnish = "Test Garnish",
        ingredients = listOf(
            Ingredient(
                name = "Ingredient 1",
                amount = Amount(
                    value = 2.0,
                    label = "oz"
                )
            ),
            Ingredient(
                name = "Ingredient 2",
                amount = Amount(
                    value = 1.0,
                    label = "tsp"
                ),
                notes = "Some notes"
            )
        ),
        createdAt = now,
        updatedAt = now
    )

    override fun setupTest() {
        getRecipeByIdUseCase = FakeGetRecipeByIdUseCase().apply {
            returnRecipe = testRecipe
        }
        saveRecipeUseCase = FakeSaveRecipeUseCase()
    }

    @Test
    fun initializingWithNewRecipeCreatesEmptyState() = testScope.runTest {
        // Given and When
        viewModel = RecipeEditViewModel(
            recipeId = 0L, // 0 indicates a new recipe
            getRecipeByIdUseCase = getRecipeByIdUseCase,
            saveRecipeUseCase = saveRecipeUseCase
        )

        // Then
        with(viewModel.uiState.value) {
            assertTrue(isNewRecipe)
            assertFalse(isLoading)
            assertEquals("", name)
            assertEquals(0, ingredients.size)
            assertEquals(0, instructions.size)
        }
    }

    @Test
    fun initializingWithExistingRecipeLoadsRecipeData() = testScope.runTest {
        // Given - setup is done with test recipe

        // When
        viewModel = RecipeEditViewModel(
            recipeId = testRecipeId,
            getRecipeByIdUseCase = getRecipeByIdUseCase,
            saveRecipeUseCase = saveRecipeUseCase
        )

        // Then
        with(viewModel.uiState.value) {
            assertFalse(isNewRecipe)
            assertFalse(isLoading)
            assertEquals(testRecipe.id, id)
            assertEquals(testRecipe.name, name)
            assertEquals(testRecipe.altName, altName)
            assertEquals(testRecipe.favorite, favorite)
            assertEquals(testRecipe.about, about)
            assertEquals(testRecipe.tags, tags)
            assertEquals(testRecipe.notes, notes)
            assertEquals(testRecipe.alcoholic, alcoholic)
            assertEquals(testRecipe.glassware, glassware)
            assertEquals(testRecipe.garnish, garnish)
            assertEquals(testRecipe.ingredients.size, ingredients.size)
            assertEquals(testRecipe.instructions.size, instructions.size)
        }
    }

    @Test
    fun loadingNonExistentRecipeShowsError() = testScope.runTest {
        // Given
        getRecipeByIdUseCase.returnRecipe = null

        // When
        viewModel = RecipeEditViewModel(
            recipeId = 999L,
            getRecipeByIdUseCase = getRecipeByIdUseCase,
            saveRecipeUseCase = saveRecipeUseCase
        )

        // Then
        assertEquals("Recipe not found", viewModel.uiState.value.error)
    }

    @Test
    fun updatingBasicFieldsChangesState() = testScope.runTest {
        // Given
        viewModel = RecipeEditViewModel(
            recipeId = 0L, // New recipe
            getRecipeByIdUseCase = getRecipeByIdUseCase,
            saveRecipeUseCase = saveRecipeUseCase
        )

        // When
        viewModel.onEvent(RecipeEditUiEvent.UpdateName("New Name"))
        viewModel.onEvent(RecipeEditUiEvent.UpdateAltName("New Alt Name"))
        viewModel.onEvent(RecipeEditUiEvent.UpdateAbout("New About"))
        viewModel.onEvent(RecipeEditUiEvent.UpdateGlassware("New Glassware"))
        viewModel.onEvent(RecipeEditUiEvent.UpdateGarnish("New Garnish"))
        viewModel.onEvent(RecipeEditUiEvent.ToggleAlcoholic)
        viewModel.onEvent(RecipeEditUiEvent.ToggleFavorite)

        // Then
        with(viewModel.uiState.value) {
            assertEquals("New Name", name)
            assertEquals("New Alt Name", altName)
            assertEquals("New About", about)
            assertEquals("New Glassware", glassware)
            assertEquals("New Garnish", garnish)
            assertEquals(false, alcoholic) // Default is true, toggled to false
            assertEquals(true, favorite) // Default is false, toggled to true
        }
    }

    @Test
    fun tagsCanBeAddedAndRemoved() = testScope.runTest {
        // Given
        viewModel = RecipeEditViewModel(
            recipeId = 0L,
            getRecipeByIdUseCase = getRecipeByIdUseCase,
            saveRecipeUseCase = saveRecipeUseCase
        )

        // When - Add tags
        viewModel.onEvent(RecipeEditUiEvent.UpdateNewTag("tag1"))
        viewModel.onEvent(RecipeEditUiEvent.AddTag)
        viewModel.onEvent(RecipeEditUiEvent.UpdateNewTag("tag2"))
        viewModel.onEvent(RecipeEditUiEvent.AddTag)

        // Then - Tags are added
        assertEquals(2, viewModel.uiState.value.tags.size)
        assertEquals("tag1", viewModel.uiState.value.tags[0])
        assertEquals("tag2", viewModel.uiState.value.tags[1])

        // When - Delete a tag
        viewModel.onEvent(RecipeEditUiEvent.DeleteTag(0))

        // Then - Tag is removed
        assertEquals(1, viewModel.uiState.value.tags.size)
        assertEquals("tag2", viewModel.uiState.value.tags[0])
    }

    @Test
    fun ingredientsCanBeAddedUpdatedAndRemoved() = testScope.runTest {
        // Given
        viewModel = RecipeEditViewModel(
            recipeId = 0L,
            getRecipeByIdUseCase = getRecipeByIdUseCase,
            saveRecipeUseCase = saveRecipeUseCase
        )

        // When - Add ingredient
        viewModel.onEvent(RecipeEditUiEvent.AddIngredient("Rum"))

        // Then
        assertEquals(1, viewModel.uiState.value.ingredients.size)
        assertEquals("Rum", viewModel.uiState.value.ingredients[0].name)

        // When - Update ingredient details
        viewModel.onEvent(RecipeEditUiEvent.UpdateIngredientAmount(0, 2.0))
        viewModel.onEvent(RecipeEditUiEvent.UpdateIngredientUnit(0, "oz"))
        viewModel.onEvent(RecipeEditUiEvent.UpdateIngredientNotes(0, "Dark rum preferred"))

        // Then
        with(viewModel.uiState.value.ingredients[0]) {
            assertEquals("Rum", name)
            assertEquals(2.0, amount)
            assertEquals("oz", unit)
            assertEquals("Dark rum preferred", notes)
        }

        // When - Add another ingredient and delete one
        viewModel.onEvent(RecipeEditUiEvent.AddIngredient("Lime"))
        viewModel.onEvent(RecipeEditUiEvent.DeleteIngredient(0))

        // Then
        assertEquals(1, viewModel.uiState.value.ingredients.size)
        assertEquals("Lime", viewModel.uiState.value.ingredients[0].name)
    }

    @Test
    fun instructionsCanBeAddedUpdatedAndRemoved() = testScope.runTest {
        // Given
        viewModel = RecipeEditViewModel(
            recipeId = 0L,
            getRecipeByIdUseCase = getRecipeByIdUseCase,
            saveRecipeUseCase = saveRecipeUseCase
        )

        // When - Add instructions
        viewModel.onEvent(RecipeEditUiEvent.AddInstruction("Step 1"))
        viewModel.onEvent(RecipeEditUiEvent.AddInstruction("Step 2"))

        // Then
        assertEquals(2, viewModel.uiState.value.instructions.size)
        assertEquals("Step 1", viewModel.uiState.value.instructions[0].value)
        assertEquals(1, viewModel.uiState.value.instructions[0].step)
        assertEquals("Step 2", viewModel.uiState.value.instructions[1].value)
        assertEquals(2, viewModel.uiState.value.instructions[1].step)

        // When - Update instruction
        viewModel.onEvent(RecipeEditUiEvent.UpdateInstructionValue(0, "Updated Step 1"))

        // Then
        assertEquals("Updated Step 1", viewModel.uiState.value.instructions[0].value)

        // When - Delete instruction
        viewModel.onEvent(RecipeEditUiEvent.DeleteInstruction(0))

        // Then
        assertEquals(1, viewModel.uiState.value.instructions.size)
        assertEquals("Step 2", viewModel.uiState.value.instructions[0].value)
        assertEquals(1, viewModel.uiState.value.instructions[0].step) // Step number should be renumbered
    }

    @Test
    fun saveRecipeValidatesAndSaves() = testScope.runTest {
        // Given
        viewModel = RecipeEditViewModel(
            recipeId = 0L,
            getRecipeByIdUseCase = getRecipeByIdUseCase,
            saveRecipeUseCase = saveRecipeUseCase
        )
        saveRecipeUseCase.result = SaveRecipeUseCase.Result.Success(42L)

        // When - Try to save without required fields
        viewModel.onEvent(RecipeEditUiEvent.SaveRecipe)

        // Then - Should have validation errors
        assertTrue(viewModel.uiState.value.validationErrors.isNotEmpty())
        assertNull(viewModel.navigationEvent.value)

        // When - Add required fields and try again
        viewModel.onEvent(RecipeEditUiEvent.UpdateName("Valid Name"))
        viewModel.onEvent(RecipeEditUiEvent.AddIngredient("Required Ingredient"))
        viewModel.onEvent(RecipeEditUiEvent.SaveRecipe)

        // Then - Should save and navigate
        advanceUntilIdle() // Make sure all coroutines complete
        assertEquals(SaveRecipeUseCase.Result.Success(42L), saveRecipeUseCase.result)
        assertTrue(saveRecipeUseCase.wasSaveCalled)
        assertEquals(RecipeEditNavigationEvent.NavigateToDetail(42L), viewModel.navigationEvent.value)
    }

    @Test
    fun saveRecipeHandlesErrors() = testScope.runTest {
        // Given
        viewModel = RecipeEditViewModel(
            recipeId = 0L,
            getRecipeByIdUseCase = getRecipeByIdUseCase,
            saveRecipeUseCase = saveRecipeUseCase
        )

        // Add required fields
        viewModel.onEvent(RecipeEditUiEvent.UpdateName("Valid Name"))
        viewModel.onEvent(RecipeEditUiEvent.AddIngredient("Required Ingredient"))

        // Set error response
        saveRecipeUseCase.result = SaveRecipeUseCase.Result.Error

        // When
        viewModel.onEvent(RecipeEditUiEvent.SaveRecipe)
        advanceUntilIdle()

        // Then
        assertTrue(saveRecipeUseCase.wasSaveCalled)
        assertNotNull(viewModel.uiState.value.error)
        assertNull(viewModel.navigationEvent.value)
    }

    @Test
    fun cancelNavigatesBack() = testScope.runTest {
        // Given
        viewModel = RecipeEditViewModel(
            recipeId = 0L,
            getRecipeByIdUseCase = getRecipeByIdUseCase,
            saveRecipeUseCase = saveRecipeUseCase
        )

        // When
        viewModel.onEvent(RecipeEditUiEvent.Cancel)

        // Then
        assertEquals(RecipeEditNavigationEvent.NavigateBack, viewModel.navigationEvent.value)
    }

    @Test
    fun consumeNavigationEventClearsEvent() = testScope.runTest {
        // Given
        viewModel = RecipeEditViewModel(
            recipeId = 0L,
            getRecipeByIdUseCase = getRecipeByIdUseCase,
            saveRecipeUseCase = saveRecipeUseCase
        )
        viewModel.onEvent(RecipeEditUiEvent.Cancel)
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

class FakeSaveRecipeUseCase : SaveRecipeUseCase {
    var result: SaveRecipeUseCase.Result = SaveRecipeUseCase.Result.Success(1L)
    var wasSaveCalled = false
    var lastSavedRecipe: Recipe? = null

    override suspend fun invoke(recipe: Recipe): SaveRecipeUseCase.Result {
        wasSaveCalled = true
        lastSavedRecipe = recipe
        return result
    }
}