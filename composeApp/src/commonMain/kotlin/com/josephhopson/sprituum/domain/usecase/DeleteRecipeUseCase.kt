package com.josephhopson.sprituum.domain.usecase

/**
 * Use case for deleting a recipe
 */
interface DeleteRecipeUseCase {
    /**
     * Deletes a recipe by its ID
     * @param id The ID of the recipe to delete
     * @return true if the recipe was deleted successfully, false otherwise
     */
    suspend operator fun invoke(id: Long): Boolean
}