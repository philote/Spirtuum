package com.josephhopson.sprituum.data.repository

import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Fake implementation of RecipeRepository for testing
 */
class FakeRecipeRepository : RecipeRepository {

    // In-memory storage of recipes
    private val recipesFlow = MutableStateFlow<Map<Long, Recipe>>(emptyMap())

    // Counter for generating recipe IDs
    private var currentId: Long = 1

    override fun getRecipes(): Flow<List<Recipe>> =
        recipesFlow.map { it.values.toList() }

    override suspend fun getRecipeById(id: Long): Recipe? =
        recipesFlow.value[id]

    override suspend fun saveRecipe(recipe: Recipe): Long {
        val recipeId = if (recipe.id == 0L) currentId++ else recipe.id
        val updatedRecipe = recipe.copy(id = recipeId)

        recipesFlow.value = recipesFlow.value + (recipeId to updatedRecipe)
        return recipeId
    }

    override suspend fun deleteRecipe(id: Long): Boolean {
        if (!recipesFlow.value.containsKey(id)) return false

        recipesFlow.value = recipesFlow.value - id
        return true
    }

    override suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean): Boolean {
        val recipe = recipesFlow.value[id] ?: return false

        val updatedRecipe = recipe.copy(favorite = isFavorite)
        recipesFlow.value = recipesFlow.value + (id to updatedRecipe)
        return true
    }

    override fun searchRecipes(query: String): Flow<List<Recipe>> {
        val lowerQuery = query.lowercase().trim()
        return recipesFlow.map { map ->
            map.values.filter { recipe ->
                recipe.name.lowercase().contains(lowerQuery) ||
                        recipe.altName?.lowercase()?.contains(lowerQuery) == true ||
                        recipe.about?.lowercase()?.contains(lowerQuery) == true ||
                        recipe.tags.any { it.lowercase().contains(lowerQuery) }
            }
        }
    }

    override fun getFavoriteRecipes(): Flow<List<Recipe>> =
        recipesFlow.map { it.values.filter { recipe -> recipe.favorite } }
}
