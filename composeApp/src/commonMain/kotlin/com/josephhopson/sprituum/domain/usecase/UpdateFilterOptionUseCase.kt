package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.FilterOption

/**
 * Use case for updating the filter option
 */
interface UpdateFilterOptionUseCase {
    /**
     * Updates the filter option used for recipes
     * @param filterOption The filter option to set
     */
    suspend operator fun invoke(filterOption: FilterOption)
}