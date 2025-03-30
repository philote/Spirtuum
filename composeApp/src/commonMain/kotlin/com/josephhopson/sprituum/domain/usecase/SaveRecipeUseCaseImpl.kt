package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.domain.repository.RecipeRepository
import com.josephhopson.sprituum.domain.usecase.SaveRecipeUseCase.Result

/**
 * Implementation of SaveRecipeUseCase
 */
class SaveRecipeUseCaseImpl(
    private val recipeRepository: RecipeRepository
) : SaveRecipeUseCase {

    override suspend fun invoke(recipe: Recipe): Result {
        // Validate recipe
        if (!recipe.isValid()) {
            return Result.InvalidRecipe
        }

        return try {
            val id = recipeRepository.saveRecipe(recipe)
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error
        }
    }
}