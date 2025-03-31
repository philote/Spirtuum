package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.repository.RecipeRepository

/**
 * Implementation of DeleteRecipeUseCase
 */
class DeleteRecipeUseCaseImpl(
    private val recipeRepository: RecipeRepository
) : DeleteRecipeUseCase {

    override suspend fun invoke(id: Long): Boolean {
        return recipeRepository.deleteRecipe(id)
    }
}