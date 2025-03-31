package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.SortOption

/**
 * Use case for updating the sort option in user preferences
 */
class UpdateSortOptionUseCaseImpl(
    private val userPreferencesRepository: UserPreferencesRepository
) : UpdateSortOptionUseCase {

    override suspend fun invoke(sortOption: SortOption) {
        userPreferencesRepository.setSortOption(sortOption)
    }
}