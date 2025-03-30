package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting all recipes with applied sorting and filtering
 */
interface GetRecipesUseCase {
    /**
     * Gets all recipes as a flow, sorted and filtered according to user preferences
     * @return Flow of sorted and filtered recipes
     */
    operator fun invoke(): Flow<List<Recipe>>
}