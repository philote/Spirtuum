package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting favorite recipes
 */
interface GetFavoriteRecipesUseCase {
    /**
     * Gets all recipes marked as favorite
     * @return Flow of favorite recipes
     */
    operator fun invoke(): Flow<List<Recipe>>
}