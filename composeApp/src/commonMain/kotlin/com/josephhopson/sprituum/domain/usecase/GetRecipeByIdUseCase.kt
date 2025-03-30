package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.model.Recipe

/**
 * Use case for getting a recipe by ID
 */
interface GetRecipeByIdUseCase {
    /**
     * Gets a recipe by its ID
     * @param id The recipe ID
     * @return The recipe if found, null otherwise
     */
    suspend operator fun invoke(id: Long): Recipe?
}