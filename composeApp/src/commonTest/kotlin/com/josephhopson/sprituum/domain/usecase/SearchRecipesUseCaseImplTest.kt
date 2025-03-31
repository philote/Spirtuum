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
class SearchRecipesUseCaseImplTest {

    private lateinit var searchRecipesUseCase: SearchRecipesUseCase
    private lateinit var mockRepository: FakeRecipeRepository

    private val recipes = listOf(
        Recipe(
            id = 1L,
            name = "Mojito",
            about = "A refreshing Cuban cocktail",
            tags = listOf("Cuban", "Refreshing"),
            alcoholic = true,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        ),
        Recipe(
            id = 2L,
            name = "Virgin Colada",
            about = "A non-alcoholic tropical drink",
            tags = listOf("Non-alcoholic", "Tropical"),
            alcoholic = false,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        ),
        Recipe(
            id = 3L,
            name = "Mai Tai",
            about = "A tropical rum cocktail",
            tags = listOf("Tropical", "Strong"),
            alcoholic = true,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    )

    @BeforeTest
    fun setup() {
        mockRepository = FakeRecipeRepository()
        searchRecipesUseCase = SearchRecipesUseCaseImpl(mockRepository)

        // Add test recipes to repository
        recipes.forEach { mockRepository.addRecipe(it) }
    }

    @Test
    fun test_search_by_name() = runTest {
        val results = searchRecipesUseCase("mojito").first()

        assertEquals(1, results.size)
        assertEquals("Mojito", results[0].name)
    }

    @Test
    fun test_search_by_partial_name() = runTest {
        val results = searchRecipesUseCase("moj").first()

        assertEquals(1, results.size)
        assertEquals("Mojito", results[0].name)
    }

    @Test
    fun test_search_case_insensitive() = runTest {
        val results = searchRecipesUseCase("MOJITO").first()

        assertEquals(1, results.size)
        assertEquals("Mojito", results[0].name)
    }

    @Test
    fun test_search_by_about() = runTest {
        val results = searchRecipesUseCase("cuban").first()

        assertEquals(1, results.size)
        assertEquals("Mojito", results[0].name)
    }

    @Test
    fun test_search_by_tag() = runTest {
        val results = searchRecipesUseCase("tropical").first()

        assertEquals(2, results.size)
        val names = results.map { it.name }
        assertTrue(names.contains("Virgin Colada"))
        assertTrue(names.contains("Mai Tai"))
    }

    @Test
    fun test_search_trims_whitespace() = runTest {
        val results = searchRecipesUseCase(" mojito ").first()

        assertEquals(1, results.size)
        assertEquals("Mojito", results[0].name)
    }

    @Test
    fun test_search_no_results() = runTest {
        val results = searchRecipesUseCase("martini").first()

        assertEquals(0, results.size)
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

    private fun assertTrue(condition: Boolean) {
        kotlin.test.assertTrue(condition, "Expected true but was false")
    }
}