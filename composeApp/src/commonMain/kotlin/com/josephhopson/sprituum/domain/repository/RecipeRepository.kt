package com.josephhopson.sprituum.domain.repository

import com.josephhopson.sprituum.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Recipe data
 *
 * This interface defines the contract for accessing recipe data.
 * Following the repository pattern, it abstracts the data sources and provides
 * a clean API for the domain layer.
 */
interface RecipeRepository {
    /**
     * Gets all recipes as a Flow
     * @return Flow of List<Recipe>
     */
    fun getRecipes(): Flow<List<Recipe>>

    /**
     * Gets a specific recipe by its ID
     * @param id The recipe ID
     * @return The recipe if found, null otherwise
     */
    suspend fun getRecipeById(id: Long): Recipe?

    /**
     * Inserts or updates a recipe
     * @param recipe The recipe to save
     * @return The ID of the saved recipe
     */
    suspend fun saveRecipe(recipe: Recipe): Long

    /**
     * Deletes a recipe by its ID
     * @param id The recipe ID to delete
     * @return true if the recipe was deleted, false otherwise
     */
    suspend fun deleteRecipe(id: Long): Boolean

    /**
     * Updates the favorite status of a recipe
     * @param id The recipe ID
     * @param isFavorite The new favorite status
     * @return true if the recipe was updated, false otherwise
     */
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean): Boolean

    /**
     * Searches recipes by name, tags, or about text
     * @param query The search query
     * @return Flow of search results
     */
    fun searchRecipes(query: String): Flow<List<Recipe>>

    /**
     * Gets all recipes that are marked as favorite
     * @return Flow of favorite recipes
     */
    fun getFavoriteRecipes(): Flow<List<Recipe>>
}
