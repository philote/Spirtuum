package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of SearchRecipesUseCase
 */
class SearchRecipesUseCaseImpl(
    private val recipeRepository: RecipeRepository
) : SearchRecipesUseCase {

    override fun invoke(query: String): Flow<List<Recipe>> {
        // Delegate to repository search function
        return recipeRepository.searchRecipes(query.trim())
    }
}