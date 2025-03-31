package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.repository.RecipeRepository

/**
 * Implementation of ToggleFavoriteRecipeUseCase
 */
class ToggleFavoriteRecipeUseCaseImpl(
    private val recipeRepository: RecipeRepository
) : ToggleFavoriteRecipeUseCase {

    override suspend fun invoke(id: Long): Boolean {
        val recipe = recipeRepository.getRecipeById(id) ?: return false
        return recipeRepository.updateFavoriteStatus(id, !recipe.favorite)
    }
}