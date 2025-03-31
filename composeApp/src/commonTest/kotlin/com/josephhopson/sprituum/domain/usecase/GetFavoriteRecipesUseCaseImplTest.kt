package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.domain.repository.RecipeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class GetFavoriteRecipesUseCaseImplTest {

    private lateinit var getFavoriteRecipesUseCase: GetFavoriteRecipesUseCase
    private lateinit var mockRepository: FakeRecipeRepository

    @BeforeTest
    fun setup() {
        mockRepository = FakeRecipeRepository()
        getFavoriteRecipesUseCase = GetFavoriteRecipesUseCaseImpl(mockRepository)

        // Add test recipes to repository - mix of favorite and non-favorite
        mockRepository.addRecipe(
            Recipe(
                id = 1L,
                name = "Mojito",
                favorite = true,
                alcoholic = true,
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )
        )

        mockRepository.addRecipe(
            Recipe(
                id = 2L,
                name = "Virgin Colada",
                favorite = false,
                alcoholic = false,
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )
        )

        mockRepository.addRecipe(
            Recipe(
                id = 3L,
                name = "Daiquiri",
                favorite = true,
                alcoholic = true,
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )
        )
    }

    @Test
    fun test_get_only_favorite_recipes() = runTest {
        val favorites = getFavoriteRecipesUseCase().first()

        // Should only return the favorite recipes
        assertEquals(2, favorites.size)

        // Verify all returned recipes are marked as favorite
        favorites.forEach {
            assertEquals(true, it.favorite)
        }

        // Verify the correct recipes are returned
        val names = favorites.map { it.name }
        assertEquals(true, names.contains("Mojito"))
        assertEquals(true, names.contains("Daiquiri"))
        assertEquals(false, names.contains("Virgin Colada"))
    }

    @Test
    fun test_no_favorites() = runTest {
        // Clear repository and add only non-favorites
        mockRepository.clearRecipes()
        mockRepository.addRecipe(
            Recipe(
                id = 4L,
                name = "Margarita",
                favorite = false,
                alcoholic = true,
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )
        )

        val favorites = getFavoriteRecipesUseCase().first()

        // Should be empty
        assertEquals(0, favorites.size)
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

        // Helper to clear all recipes
        fun clearRecipes() {
            recipesMap.clear()
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