package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.ViewMode

/**
 * Implementation of UpdateViewModeUseCase
 */
class UpdateViewModeUseCaseImpl(
    private val userPreferencesRepository: UserPreferencesRepository
) : UpdateViewModeUseCase {

    override suspend fun invoke(viewMode: ViewMode) {
        userPreferencesRepository.setViewMode(viewMode)
    }
}