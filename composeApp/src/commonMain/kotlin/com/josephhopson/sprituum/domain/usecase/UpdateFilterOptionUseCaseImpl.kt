package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.FilterOption

/**
 * Implementation of UpdateFilterOptionUseCase
 */
class UpdateFilterOptionUseCaseImpl(
    private val userPreferencesRepository: UserPreferencesRepository
) : UpdateFilterOptionUseCase {

    override suspend fun invoke(filterOption: FilterOption) {
        userPreferencesRepository.setFilterOption(filterOption)
    }
}