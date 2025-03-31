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
class DeleteRecipeUseCaseImplTest {

    private lateinit var deleteRecipeUseCase: DeleteRecipeUseCase
    private lateinit var mockRepository: FakeRecipeRepository

    private val testRecipe = Recipe(
        id = 1L,
        name = "Mojito",
        alcoholic = true,
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now()
    )

    @BeforeTest
    fun setup() {
        mockRepository = FakeRecipeRepository()
        deleteRecipeUseCase = DeleteRecipeUseCaseImpl(mockRepository)

        // Add test recipe to repository
        mockRepository.addRecipe(testRecipe)
    }

    @Test
    fun test_delete_existing_recipe() = runTest {
        // Verify recipe exists before deletion
        val recipeBeforeDeletion = mockRepository.getRecipeById(1L)
        assertEquals(testRecipe.id, recipeBeforeDeletion?.id)

        // Delete the recipe
        val result = deleteRecipeUseCase(1L)

        // Verify deletion was successful
        assertTrue(result)

        // Verify recipe no longer exists
        val recipeAfterDeletion = mockRepository.getRecipeById(1L)
        assertEquals(null, recipeAfterDeletion)
    }

    @Test
    fun test_delete_non_existent_recipe() = runTest {
        // Try to delete a recipe that doesn't exist
        val result = deleteRecipeUseCase(99L)

        // Verify deletion failed
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