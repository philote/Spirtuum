package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.ViewMode

/**
 * Use case for updating the view mode
 */
interface UpdateViewModeUseCase {
    /**
     * Updates the view mode used for recipe list
     * @param viewMode The view mode to set
     */
    suspend operator fun invoke(viewMode: ViewMode)
}