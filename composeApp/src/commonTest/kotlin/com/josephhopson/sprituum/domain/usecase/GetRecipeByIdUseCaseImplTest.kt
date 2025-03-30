package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.domain.repository.RecipeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExperimentalCoroutinesApi
class GetRecipeByIdUseCaseImplTest {

    private lateinit var getRecipeByIdUseCase: GetRecipeByIdUseCase
    private lateinit var mockRepository: FakeRecipeRepository

    private val testRecipe = Recipe(
        id = 1L,
        name = "Mojito",
        alcoholic = true,
        createdAt = Instant.fromEpochMilliseconds(1000),
        updatedAt = Instant.fromEpochMilliseconds(2000)
    )

    @BeforeTest
    fun setup() {
        mockRepository = FakeRecipeRepository()
        getRecipeByIdUseCase = GetRecipeByIdUseCaseImpl(mockRepository)

        // Add test recipe to repository
        mockRepository.addRecipe(testRecipe)
    }

    @Test
    fun test_get_existing_recipe_by_id() = runTest {
        val recipe = getRecipeByIdUseCase(1L)

        assertEquals(testRecipe.id, recipe?.id)
        assertEquals(testRecipe.name, recipe?.name)
    }

    @Test
    fun test_get_non_existent_recipe_by_id() = runTest {
        val recipe = getRecipeByIdUseCase(99L)

        assertNull(recipe)
    }

    /**
     * Fake RecipeRepository for testing
     */
    private class FakeRecipeRepository : RecipeRepository {
        private val recipesMap = mutableMapOf<Long, Recipe>()

        fun addRecipe(recipe: Recipe) {
            recipesMap[recipe.id] = recipe
        }

        override fun getRecipes() = flowOf(recipesMap.values.toList())

        override suspend fun getRecipeById(id: Long) = recipesMap[id]

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

        override fun searchRecipes(query: String) = flowOf(
            recipesMap.values.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.about?.contains(query, ignoreCase = true) == true ||
                        it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
            }
        )

        override fun getFavoriteRecipes() = flowOf(recipesMap.values.filter { it.favorite })
    }
}