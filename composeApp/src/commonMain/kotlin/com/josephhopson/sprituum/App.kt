package com.josephhopson.sprituum

import androidx.compose.runtime.Composable
import com.josephhopson.sprituum.theme.AppTheme
import com.josephhopson.sprituum.ui.recipelist.RecipeListScreen
import org.koin.compose.KoinContext

/**
 * Main App composable that sets up the application's UI
 */
@Composable
internal fun App() = KoinContext {
    AppTheme {
        // Navigation will be added later, for now just show the RecipeListScreen
        RecipeListScreen(
            onNavigateToDetail = { recipeId ->
                // Will be implemented when the detail screen is created
                println("Navigate to detail for recipe ID: $recipeId")
            }
        )
    }
}