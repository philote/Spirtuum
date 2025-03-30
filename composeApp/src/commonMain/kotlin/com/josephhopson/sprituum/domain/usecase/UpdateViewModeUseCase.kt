package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.ViewMode

/**
 * Use case for updating recipe view mode
 */
interface UpdateViewModeUseCase {
    /**
     * Updates the view mode used for recipes
     * @param viewMode The new view mode to set
     */
    suspend operator fun invoke(viewMode: ViewMode)
}