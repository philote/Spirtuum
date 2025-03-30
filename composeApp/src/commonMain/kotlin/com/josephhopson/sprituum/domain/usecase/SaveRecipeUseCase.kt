package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.model.Recipe

/**
 * Use case for saving a recipe
 */
interface SaveRecipeUseCase {
    /**
     * Result of save operation
     */
    sealed class Result {
        /**
         * Recipe was saved successfully
         * @param id The ID of the saved recipe
         */
        data class Success(val id: Long) : Result()

        /**
         * Recipe validation failed
         */
        data object InvalidRecipe : Result()

        /**
         * Recipe save operation failed
         */
        data object Error : Result()
    }

    /**
     * Saves a recipe (creates new or updates existing)
     * @param recipe The recipe to save
     * @return The result of the save operation
     */
    suspend operator fun invoke(recipe: Recipe): Result
}