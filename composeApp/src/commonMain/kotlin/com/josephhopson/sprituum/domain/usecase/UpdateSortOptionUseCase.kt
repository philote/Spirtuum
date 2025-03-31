package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.SortOption

/**
 * Use case for updating the sort option
 */
interface UpdateSortOptionUseCase {
    /**
     * Updates the sort option used for recipes
     * @param sortOption The sort option to set
     */
    suspend operator fun invoke(sortOption: SortOption)
}