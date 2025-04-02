package com.josephhopson.sprituum.data.source.recipes

import com.josephhopson.sprituum.domain.model.Recipe

/**
 * Provider for initial recipes to populate the app on first launch
 */
interface InitialRecipesProvider {

    /**
     * Returns a list of initial recipes to populate the app
     * @return List of Recipe objects
     */
    suspend fun getInitialRecipes(): List<Recipe>
}
