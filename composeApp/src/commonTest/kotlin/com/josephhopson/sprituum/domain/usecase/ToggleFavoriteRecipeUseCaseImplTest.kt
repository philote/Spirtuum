package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.domain.repository.RecipeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class ToggleFavoriteRecipeUseCaseImplTest {

    private lateinit var toggleFavoriteRecipeUseCase: ToggleFavoriteRecipeUseCase
    private lateinit var mockRepository: FakeRecipeRepository

    private val favoriteRecipe = Recipe(
        id = 1L,
        name = "Mojito",
        favorite = true,
        alcoholic = true,
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now()
    )

    private val nonFavoriteRecipe = Recipe(
        id = 2L,
        name = "Margarita",
        favorite = false,
        alcoholic = true,
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now()
    )

    @BeforeTest
    fun setup() {
        mockRepository = FakeRecipeRepository()
        toggleFavoriteRecipeUseCase = ToggleFavoriteRecipeUseCaseImpl(mockRepository)

        // Add test recipes to repository
        mockRepository.addRecipe(favoriteRecipe)
        mockRepository.addRecipe(nonFavoriteRecipe)
    }

    @Test
    fun test_toggle_favorite_to_unfavorite() = runTest {
        // Verify recipe is initially favorite
        val initialRecipe = mockRepository.getRecipeById(1L)
        assertEquals(true, initialRecipe?.favorite)

        // Toggle favorite status
        val result = toggleFavoriteRecipeUseCase(1L)

        // Verify toggle was successful
        assertTrue(result)

        // Verify recipe is now unfavorite
        val updatedRecipe = mockRepository.getRecipeById(1L)
        assertEquals(false, updatedRecipe?.favorite)
    }

    @Test
    fun test_toggle_unfavorite_to_favorite() = runTest {
        // Verify recipe is initially not favorite
        val initialRecipe = mockRepository.getRecipeById(2L)
        assertEquals(false, initialRecipe?.favorite)

        // Toggle favorite status
        val result = toggleFavoriteRecipeUseCase(2L)

        // Verify toggle was successful
        assertTrue(result)

        // Verify recipe is now favorite
        val updatedRecipe = mockRepository.getRecipeById(2L)
        assertEquals(true, updatedRecipe?.favorite)
    }

    @Test
    fun test_toggle_non_existent_recipe() = runTest {
        // Try to toggle favorite status for a recipe that doesn't exist
        val result = toggleFavoriteRecipeUseCase(99L)

        // Verify toggle failed
        assertFalse(result)
    }

    /**
     * Fake RecipeRepository for testing
     */
    private class FakeRecipeRepository : RecipeRepository {
        private val recipesMap = mutableMapOf<Long, Recipe>()

        // Helper method for tests to add recipes directly
        fun addRecipe(recipe: Recipe) {
            recipesMap[recipe.id] = recipe
        }

        override fun getRecipes(): Flow<List<Recipe>> = flowOf(recipesMap.values.toList())

        override suspend fun getRecipeById(id: Long): Recipe? = recipesMap[id]

        override suspend fun saveRecipe(recipe: Recipe): Long {
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