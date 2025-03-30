package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.domain.repository.RecipeRepository
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.FilterOption
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.SortOption
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class GetRecipesUseCaseImplTest {

    private lateinit var getRecipesUseCase: GetRecipesUseCase
    private lateinit var mockRecipeRepository: FakeRecipeRepository
    private lateinit var mockUserPreferencesRepository: FakeUserPreferencesRepository

    private val testRecipes = listOf(
        Recipe(
            id = 1L,
            name = "Mojito",
            alcoholic = true,
            createdAt = Instant.fromEpochMilliseconds(1000),
            updatedAt = Instant.fromEpochMilliseconds(1000)
        ),
        Recipe(
            id = 2L,
            name = "Virgin Colada",
            alcoholic = false,
            createdAt = Instant.fromEpochMilliseconds(2000),
            updatedAt = Instant.fromEpochMilliseconds(3000)
        ),
        Recipe(
            id = 3L,
            name = "Daiquiri",
            favorite = true,
            alcoholic = true,
            createdAt = Instant.fromEpochMilliseconds(3000),
            updatedAt = Instant.fromEpochMilliseconds(2000)
        )
    )

    @BeforeTest
    fun setup() {
        mockRecipeRepository = FakeRecipeRepository()
        mockUserPreferencesRepository = FakeUserPreferencesRepository()
        getRecipesUseCase = GetRecipesUseCaseImpl(mockRecipeRepository, mockUserPreferencesRepository)

        // Populate test data
        testRecipes.forEach { mockRecipeRepository.addRecipe(it) }
    }

    @Test
    fun test_get_all_recipes_with_default_settings() = runTest {
        // Default settings: ALL filter, NAME_ASC sorting
        mockUserPreferencesRepository.setFilterOption(FilterOption.ALL)
        mockUserPreferencesRepository.setSortOption(SortOption.NAME_ASC)

        val recipes = getRecipesUseCase().first()

        assertEquals(3, recipes.size)
        // Verify sorted by name ascending
        assertEquals("Daiquiri", recipes[0].name)
        assertEquals("Mojito", recipes[1].name)
        assertEquals("Virgin Colada", recipes[2].name)
    }

    @Test
    fun test_get_recipes_with_name_descending_sort() = runTest {
        mockUserPreferencesRepository.setFilterOption(FilterOption.ALL)
        mockUserPreferencesRepository.setSortOption(SortOption.NAME_DESC)

        val recipes = getRecipesUseCase().first()

        assertEquals(3, recipes.size)
        // Verify sorted by name descending
        assertEquals("Virgin Colada", recipes[0].name)
        assertEquals("Mojito", recipes[1].name)
        assertEquals("Daiquiri", recipes[2].name)
    }

    @Test
    fun test_get_recipes_filtered_by_favorites() = runTest {
        mockUserPreferencesRepository.setFilterOption(FilterOption.FAVORITES)
        mockUserPreferencesRepository.setSortOption(SortOption.NAME_ASC)

        val recipes = getRecipesUseCase().first()

        assertEquals(1, recipes.size)
        assertEquals("Daiquiri", recipes[0].name)
        assertEquals(true, recipes[0].favorite)
    }

    @Test
    fun test_get_recipes_filtered_by_alcoholic() = runTest {
        mockUserPreferencesRepository.setFilterOption(FilterOption.ALCOHOLIC)
        mockUserPreferencesRepository.setSortOption(SortOption.NAME_ASC)

        val recipes = getRecipesUseCase().first()

        assertEquals(2, recipes.size)
        assertEquals(true, recipes[0].alcoholic)
        assertEquals(true, recipes[1].alcoholic)
    }

    @Test
    fun test_get_recipes_filtered_by_non_alcoholic() = runTest {
        mockUserPreferencesRepository.setFilterOption(FilterOption.NON_ALCOHOLIC)
        mockUserPreferencesRepository.setSortOption(SortOption.NAME_ASC)

        val recipes = getRecipesUseCase().first()

        assertEquals(1, recipes.size)
        assertEquals(false, recipes[0].alcoholic)
    }

    @Test
    fun test_get_recipes_sorted_by_date_created_newest() = runTest {
        mockUserPreferencesRepository.setFilterOption(FilterOption.ALL)
        mockUserPreferencesRepository.setSortOption(SortOption.DATE_CREATED_NEWEST)

        val recipes = getRecipesUseCase().first()

        assertEquals(3, recipes.size)
        // Verify sorted by creation date descending
        assertEquals(3L, recipes[0].id) // Created at 3000
        assertEquals(2L, recipes[1].id) // Created at 2000
        assertEquals(1L, recipes[2].id) // Created at 1000
    }

    @Test
    fun test_get_recipes_sorted_by_date_updated_newest() = runTest {
        mockUserPreferencesRepository.setFilterOption(FilterOption.ALL)
        mockUserPreferencesRepository.setSortOption(SortOption.DATE_UPDATED_NEWEST)

        val recipes = getRecipesUseCase().first()

        assertEquals(3, recipes.size)
        // Verify sorted by update date descending
        assertEquals(2L, recipes[0].id) // Updated at 3000
        assertEquals(3L, recipes[1].id) // Updated at 2000
        assertEquals(1L, recipes[2].id) // Updated at 1000
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

    /**
     * Fake UserPreferencesRepository for testing
     */
    private class FakeUserPreferencesRepository : UserPreferencesRepository {
        private var sortOption = SortOption.NAME_ASC
        private var filterOption = FilterOption.ALL
        private var viewMode = UserPreferencesRepository.ViewMode.LIST

        override fun getSortOption() = flowOf(sortOption)

        override suspend fun setSortOption(sortOption: SortOption) {
            this.sortOption = sortOption
        }

        override fun getFilterOption() = flowOf(filterOption)

        override suspend fun setFilterOption(filterOption: FilterOption) {
            this.filterOption = filterOption
        }

        override fun getViewMode() = flowOf(viewMode)

        override suspend fun setViewMode(viewMode: UserPreferencesRepository.ViewMode) {
            this.viewMode = viewMode
        }
    }
}