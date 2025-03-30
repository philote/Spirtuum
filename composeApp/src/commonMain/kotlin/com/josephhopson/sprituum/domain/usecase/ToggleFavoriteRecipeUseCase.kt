package com.josephhopson.sprituum.domain.usecase

/**
 * Use case for toggling the favorite status of a recipe
 */
interface ToggleFavoriteRecipeUseCase {
    /**
     * Toggles the favorite status of a recipe
     * @param id The ID of the recipe
     * @return true if the operation was successful, false otherwise
     */
    suspend operator fun invoke(id: Long): Boolean
}