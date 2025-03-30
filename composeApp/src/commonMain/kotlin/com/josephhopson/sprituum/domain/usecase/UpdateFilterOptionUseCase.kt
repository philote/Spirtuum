package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.FilterOption

/**
 * Use case for updating recipe filter option
 */
interface UpdateFilterOptionUseCase {
    /**
     * Updates the filter option used for recipes
     * @param filterOption The new filter option to set
     */
    suspend operator fun invoke(filterOption: FilterOption)
}