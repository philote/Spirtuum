package com.josephhopson.sprituum.domain.usecase

/**
 * Use case to initialize the app with starter recipes on first launch
 */
interface InitializeAppDataUseCase {
    /**
     * Checks if the app is on first launch and populates initial data if needed
     */
    suspend fun initializeAppData()
}
