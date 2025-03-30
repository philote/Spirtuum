package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.SortOption

/**
 * Use case for updating recipe sort option
 */
interface UpdateSortOptionUseCase {
    /**
     * Updates the sort option used for recipes
     * @param sortOption The new sort option to set
     */
    suspend operator fun invoke(sortOption: SortOption)
}