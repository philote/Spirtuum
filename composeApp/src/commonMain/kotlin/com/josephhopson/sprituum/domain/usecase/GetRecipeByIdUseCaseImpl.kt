package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.domain.repository.RecipeRepository

/**
 * Implementation of GetRecipeByIdUseCase
 */
class GetRecipeByIdUseCaseImpl(
    private val recipeRepository: RecipeRepository
) : GetRecipeByIdUseCase {

    override suspend fun invoke(id: Long): Recipe? {
        return recipeRepository.getRecipeById(id)
    }
}