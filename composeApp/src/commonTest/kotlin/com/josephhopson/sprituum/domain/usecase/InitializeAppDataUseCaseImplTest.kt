package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.data.source.recipes.InitialRecipesProvider
import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.domain.repository.RecipeRepository
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class InitializeAppDataUseCaseImplTest {

    @Test
    fun `initializeAppData should populate recipes on first launch when db is empty`() = runTest {
        // Given
        val testRepository = TestRecipeRepository()
        val testPreferences = TestUserPreferencesRepository(isFirstLaunchFlag = true)
        val testProvider = TestInitialRecipesProvider()

        // Recipe count should be zero at start
        assertEquals(0, testRepository.recipeCount)

        val useCase = InitializeAppDataUseCaseImpl(
            userPreferencesRepository = testPreferences,
            recipeRepository = testRepository,
            initialRecipesProvider = testProvider
        )

        // When
        useCase.initializeAppData()

        // Then
        // Should have saved the test recipes
        assertEquals(testProvider.testRecipes.size, testRepository.recipeCount)
        // Should have marked first launch as completed
        assertFalse(testPreferences.isFirstLaunchFlag, "First launch should be marked as completed")
    }

    @Test
    fun `initializeAppData should not populate recipes when not first launch`() = runTest {
        // Given
        val testRepository = TestRecipeRepository()
        val testPreferences = TestUserPreferencesRepository(isFirstLaunchFlag = false)
        val testProvider = TestInitialRecipesProvider()

        // Recipe count should be zero at start
        assertEquals(0, testRepository.recipeCount)

        val useCase = InitializeAppDataUseCaseImpl(
            userPreferencesRepository = testPreferences,
            recipeRepository = testRepository,
            initialRecipesProvider = testProvider
        )

        // When
        useCase.initializeAppData()

        // Then
        // Should not have saved any recipes
        assertEquals(
            0,
            testRepository.recipeCount,
            "No recipes should be saved when not first launch"
        )
    }

    @Test
    fun `initializeAppData should not populate recipes when db is not empty`() = runTest {
        // Given
        val testRepository = TestRecipeRepository()
        val testPreferences = TestUserPreferencesRepository(isFirstLaunchFlag = true)
        val testProvider = TestInitialRecipesProvider()

        // Add a recipe to make DB non-empty
        testRepository.addTestRecipe()
        assertEquals(1, testRepository.recipeCount)

        val useCase = InitializeAppDataUseCaseImpl(
            userPreferencesRepository = testPreferences,
            recipeRepository = testRepository,
            initialRecipesProvider = testProvider
        )

        // When
        useCase.initializeAppData()

        // Then
        // Should not have saved any additional recipes
        assertEquals(
            1,
            testRepository.recipeCount,
            "No recipes should be added when DB is not empty"
        )
        // Should still mark first launch as completed
        assertFalse(testPreferences.isFirstLaunchFlag, "First launch should be marked as completed")
    }

    /**
     * Test implementation of RecipeRepository
     */
    private class TestRecipeRepository : RecipeRepository {
        private val recipes = mutableListOf<Recipe>()
        val recipeCount: Int get() = recipes.size

        fun addTestRecipe() {
            recipes.add(
                Recipe(
                    id = 1L,
                    name = "Test Recipe",
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                )
            )
        }

        override fun getRecipes(): Flow<List<Recipe>> = flowOf(recipes)

        override suspend fun getRecipeById(id: Long): Recipe? = recipes.find { it.id == id }

        override suspend fun saveRecipe(recipe: Recipe): Long {
            val id = if (recipe.id == 0L) (recipes.size + 1).toLong() else recipe.id
            val savedRecipe = if (recipe.id == 0L) recipe.copy(id = id) else recipe
            recipes.add(savedRecipe)
            return id
        }

        override suspend fun deleteRecipe(id: Long): Boolean {
            val initialSize = recipes.size
            recipes.removeAll { it.id == id }
            return recipes.size < initialSize
        }

        override suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean): Boolean {
            val recipe = recipes.find { it.id == id } ?: return false
            val index = recipes.indexOf(recipe)
            recipes[index] = recipe.copy(favorite = isFavorite)
            return true
        }

        override fun searchRecipes(query: String): Flow<List<Recipe>> {
            return flowOf(recipes.filter { it.name.contains(query, ignoreCase = true) })
        }

        override fun getFavoriteRecipes(): Flow<List<Recipe>> {
            return flowOf(recipes.filter { it.favorite })
        }
    }

    /**
     * Test implementation of UserPreferencesRepository
     */
    private class TestUserPreferencesRepository(var isFirstLaunchFlag: Boolean) :
        UserPreferencesRepository {
        override fun getSortOption(): Flow<UserPreferencesRepository.SortOption> {
            return flowOf(UserPreferencesRepository.SortOption.NAME_ASC)
        }

        override suspend fun setSortOption(sortOption: UserPreferencesRepository.SortOption) {
            // No-op for test
        }

        override fun getFilterOption(): Flow<UserPreferencesRepository.FilterOption> {
            return flowOf(UserPreferencesRepository.FilterOption.ALL)
        }

        override suspend fun setFilterOption(filterOption: UserPreferencesRepository.FilterOption) {
            // No-op for test
        }

        override fun getViewMode(): Flow<UserPreferencesRepository.ViewMode> {
            return flowOf(UserPreferencesRepository.ViewMode.LIST)
        }

        override suspend fun setViewMode(viewMode: UserPreferencesRepository.ViewMode) {
            // No-op for test
        }

        override fun isFirstLaunch(): Flow<Boolean> {
            return flowOf(isFirstLaunchFlag)
        }

        override suspend fun markFirstLaunchComplete() {
            isFirstLaunchFlag = false
        }
    }

    /**
     * Test implementation of InitialRecipesProvider
     */
    private class TestInitialRecipesProvider : InitialRecipesProvider {
        val testRecipes = listOf(
            Recipe(
                id = 0L,
                name = "Test Recipe 1",
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            ),
            Recipe(
                id = 0L,
                name = "Test Recipe 2",
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )
        )

        override suspend fun getInitialRecipes(): List<Recipe> {
            return testRecipes
        }
    }
}
