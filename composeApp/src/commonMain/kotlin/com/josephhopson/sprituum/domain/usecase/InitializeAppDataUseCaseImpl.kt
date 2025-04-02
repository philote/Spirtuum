package com.josephhopson.sprituum.domain.usecase

import co.touchlab.kermit.Logger
import com.josephhopson.sprituum.data.source.recipes.InitialRecipesProvider
import com.josephhopson.sprituum.domain.repository.RecipeRepository
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first

private val logger = Logger.withTag("InitializeAppDataUseCase")

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
        logger.d { "Starting initialization" }
        val isFirstLaunch = userPreferencesRepository.isFirstLaunch().first()
        logger.d { "Is first launch? $isFirstLaunch" }

        if (isFirstLaunch) {
            // Check if the database is actually empty
            val existingRecipes = recipeRepository.getRecipes().first()
            logger.d { "Found ${existingRecipes.size} existing recipes" }

            if (existingRecipes.isEmpty()) {
                logger.i { "Loading initial recipes" }
                // Load initial recipes
                val recipes = initialRecipesProvider.getInitialRecipes()
                logger.i { "Loaded ${recipes.size} initial recipes" }

                // Save each recipe to the database
                recipes.forEach { recipe ->
                    logger.d { "Saving recipe: ${recipe.name}" }
                    recipeRepository.saveRecipe(recipe)
                }
                logger.i { "All recipes saved successfully" }
            } else {
                logger.i { "Skipping recipe initialization as database already has data" }
            }

            // Mark first launch as completed
            logger.d { "Marking first launch as complete" }
            userPreferencesRepository.markFirstLaunchComplete()
        } else {
            logger.i { "Skipping initialization as this is not the first launch" }
        }
        logger.i { "Initialization completed" }
    }
}
