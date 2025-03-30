package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.domain.repository.RecipeRepository
import com.josephhopson.sprituum.domain.usecase.SaveRecipeUseCase.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class SaveRecipeUseCaseImplTest {

    private lateinit var saveRecipeUseCase: SaveRecipeUseCase
    private lateinit var mockRepository: FakeRecipeRepository

    @BeforeTest
    fun setup() {
        mockRepository = FakeRecipeRepository()
        saveRecipeUseCase = SaveRecipeUseCaseImpl(mockRepository)
    }

    @Test
    fun test_save_valid_recipe_successfully() = runTest {
        val now = Clock.System.now()
        val recipe = Recipe(
            name = "Mojito",
            ingredients = listOf(
                com.josephhopson.sprituum.domain.model.Ingredient(name = "White rum")
            ),
            createdAt = now,
            updatedAt = now
        )

        val result = saveRecipeUseCase(recipe)

        assertTrue(result is Result.Success)
        assertEquals(1, result.id)
        assertEquals(1, mockRepository.getRecipeCount())
    }

    @Test
    fun test_update_existing_recipe() = runTest {
        val now = Clock.System.now()
        val recipe = Recipe(
            id = 1L,
            name = "Original Recipe",
            ingredients = listOf(
                com.josephhopson.sprituum.domain.model.Ingredient(name = "Ingredient")
            ),
            createdAt = now,
            updatedAt = now
        )

        // First save the original recipe
        mockRepository.saveRecipe(recipe)

        // Now update it
        val updatedRecipe = recipe.copy(name = "Updated Recipe")
        val result = saveRecipeUseCase(updatedRecipe)

        assertTrue(result is Result.Success)
        assertEquals(1L, result.id)
        assertEquals(1, mockRepository.getRecipeCount())
        assertEquals("Updated Recipe", mockRepository.getRecipeById(1L)?.name)
    }

    @Test
    fun test_save_invalid_recipe_fails() = runTest {
        val now = Clock.System.now()

        // Invalid recipe without name
        val invalidRecipe1 = Recipe(
            name = "",
            ingredients = listOf(
                com.josephhopson.sprituum.domain.model.Ingredient(name = "Ingredient")
            ),
            createdAt = now,
            updatedAt = now
        )

        val result1 = saveRecipeUseCase(invalidRecipe1)
        assertTrue(result1 is Result.InvalidRecipe)

        // Invalid recipe without ingredients
        val invalidRecipe2 = Recipe(
            name = "Recipe Name",
            ingredients = emptyList(),
            createdAt = now,
            updatedAt = now
        )

        val result2 = saveRecipeUseCase(invalidRecipe2)
        assertTrue(result2 is Result.InvalidRecipe)

        // Verify no recipes were saved
        assertEquals(0, mockRepository.getRecipeCount())
    }

    @Test
    fun test_save_recipe_with_error() = runTest {
        val now = Clock.System.now()
        mockRepository.setShouldThrowError(true)

        val recipe = Recipe(
            name = "Mojito",
            ingredients = listOf(
                com.josephhopson.sprituum.domain.model.Ingredient(name = "White rum")
            ),
            createdAt = now,
            updatedAt = now
        )

        val result = saveRecipeUseCase(recipe)
        assertTrue(result is Result.Error)
    }

    /**
     * Fake RecipeRepository for testing
     */
    private class FakeRecipeRepository : RecipeRepository {
        private val recipesMap = mutableMapOf<Long, Recipe>()
        private var shouldThrowError = false

        fun getRecipeCount() = recipesMap.size

        fun setShouldThrowError(value: Boolean) {
            shouldThrowError = value
        }

        override fun getRecipes(): Flow<List<Recipe>> = flowOf(recipesMap.values.toList())

        override suspend fun getRecipeById(id: Long): Recipe? = recipesMap[id]

        override suspend fun saveRecipe(recipe: Recipe): Long {
            if (shouldThrowError) {
                throw RuntimeException("Simulated error")
            }

            val id = recipe.id.takeIf { it > 0 } ?: ((recipesMap.keys.maxOrNull() ?: 0) + 1)
            val updatedRecipe = if (recipe.id == 0L) recipe.copy(id = id) else recipe
            recipesMap[id] = updatedRecipe
            return id
        }

        override suspend fun deleteRecipe(id: Long): Boolean {
            return recipesMap.remove(id) != null
        }

        override suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean): Boolean {
            val recipe = recipesMap[id] ?: return false
            recipesMap[id] = recipe.copy(favorite = isFavorite)
            return true
        }

        override fun searchRecipes(query: String): Flow<List<Recipe>> = flowOf(
            recipesMap.values.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.about?.contains(query, ignoreCase = true) == true ||
                        it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
            }
        )

        override fun getFavoriteRecipes(): Flow<List<Recipe>> = flowOf(
            recipesMap.values.filter { it.favorite }
        )
    }
}