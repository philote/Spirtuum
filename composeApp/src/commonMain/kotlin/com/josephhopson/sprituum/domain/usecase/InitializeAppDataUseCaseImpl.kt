package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.data.source.recipes.InitialRecipesProvider
import com.josephhopson.sprituum.domain.repository.RecipeRepository
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first

/**
 * Implementation of InitializeAppDataUseCase
 */
class InitializeAppDataUseCaseImpl(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val recipeRepository: RecipeRepository,
    private val initialRecipesProvider: InitialRecipesProvider
) : InitializeAppDataUseCase {

    /**
     * Checks if this is the first launch of the app and populates initial data if needed
     */
    override suspend fun initializeAppData() {
        val isFirstLaunch = userPreferencesRepository.isFirstLaunch().first()

        if (isFirstLaunch) {
            // Check if the database is actually empty
            val existingRecipes = recipeRepository.getRecipes().first()

            if (existingRecipes.isEmpty()) {
                // Load initial recipes
                val recipes = initialRecipesProvider.getInitialRecipes()

                // Save each recipe to the database
                recipes.forEach { recipe ->
                    recipeRepository.saveRecipe(recipe)
                }
            }

            // Mark first launch as completed
            userPreferencesRepository.markFirstLaunchComplete()
        }
    }
}
