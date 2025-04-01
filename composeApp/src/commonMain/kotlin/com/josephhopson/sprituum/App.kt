package com.josephhopson.sprituum

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.josephhopson.sprituum.theme.AppTheme
import com.josephhopson.sprituum.ui.recipedetail.RecipeDetailScreen
import com.josephhopson.sprituum.ui.recipelist.RecipeListScreen
import org.koin.compose.KoinContext

/**
 * Main App composable that sets up the application's UI
 */
@Composable
internal fun App() = KoinContext {
    AppTheme {
        val navController = rememberNavController()

        when (val currentScreen = navController.currentScreen) {
            is Screen.RecipeList -> {
                RecipeListScreen(
                    onNavigateToDetail = { recipeId ->
                        navController.navigateTo(Screen.RecipeDetail(recipeId))
                    }
                )
            }
            is Screen.RecipeDetail -> {
                RecipeDetailScreen(
                    recipeId = currentScreen.recipeId,
                    onNavigateBack = { navController.navigateBack() },
                    onNavigateToEdit = { editRecipeId ->
                        // Will be implemented in a later phase
                        println("Navigate to edit recipe: $editRecipeId")
                    },
                    onShareRecipe = { recipe ->
                        shareRecipe(recipe)
                    }
                )
            }
        }
    }
}

/**
 * Simple navigation controller
 */
@Composable
fun rememberNavController(): NavController {
    return remember { NavController() }
}

/**
 * Navigation controller for managing screens
 */
class NavController {
    var currentScreen by mutableStateOf<Screen>(Screen.RecipeList)
        private set

    private val backStack = mutableListOf<Screen>()

    fun navigateTo(screen: Screen) {
        backStack.add(currentScreen)
        currentScreen = screen
    }

    fun navigateBack() {
        if (backStack.isNotEmpty()) {
            currentScreen = backStack.removeLast()
        }
    }
}

/**
 * Screen definitions for navigation
 */
sealed class Screen {
    data object RecipeList : Screen()
    data class RecipeDetail(val recipeId: Long) : Screen()
}