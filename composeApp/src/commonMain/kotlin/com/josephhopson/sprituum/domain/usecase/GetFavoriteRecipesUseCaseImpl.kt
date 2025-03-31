package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of GetFavoriteRecipesUseCase
 */
class GetFavoriteRecipesUseCaseImpl(
    private val recipeRepository: RecipeRepository
) : GetFavoriteRecipesUseCase {

    override fun invoke(): Flow<List<Recipe>> {
        return recipeRepository.getFavoriteRecipes()
    }
}