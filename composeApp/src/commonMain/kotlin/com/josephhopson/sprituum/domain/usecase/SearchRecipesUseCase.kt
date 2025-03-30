package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * Use case for searching recipes
 */
interface SearchRecipesUseCase {
    /**
     * Searches recipes based on a query string
     * @param query The search query
     * @return Flow of search results
     */
    operator fun invoke(query: String): Flow<List<Recipe>>
}