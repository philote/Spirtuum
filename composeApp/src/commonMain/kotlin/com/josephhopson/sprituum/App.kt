package com.josephhopson.sprituum

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.josephhopson.sprituum.theme.AppTheme
import com.josephhopson.sprituum.ui.recipedetail.RecipeDetailScreen
import com.josephhopson.sprituum.ui.recipeedit.RecipeEditScreen
import com.josephhopson.sprituum.ui.recipelist.RecipeListScreen
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

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
                        if (recipeId == 0L) {
                            // Create new recipe
                            navController.navigateTo(Screen.RecipeEdit(0L))
                        } else {
                            // View existing recipe
                            navController.navigateTo(Screen.RecipeDetail(recipeId))
                        }
                    }
                )
            }
            is Screen.RecipeDetail -> {
                RecipeDetailScreen(
                    recipeId = currentScreen.recipeId,
                    onNavigateBack = { navController.navigateBack() },
                    onNavigateToEdit = { editRecipeId ->
                        navController.navigateTo(Screen.RecipeEdit(editRecipeId))
                    },
                    onShareRecipe = { recipe ->
                        shareRecipe(recipe)
                    }
                )
            }
            is Screen.RecipeEdit -> {
                RecipeEditScreen(
                    viewModel = koinInject { parametersOf(currentScreen.recipeId) },
                    onNavigateBack = { navController.navigateBack() },
                    onNavigateToDetail = { newRecipeId ->
                    navController.navigateTo(Screen.RecipeDetail(newRecipeId))
                    },
                    onCapturePhoto = {
                        // Photo capture functionality will be implemented later
                        println("Photo capture triggered")
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
    data class RecipeEdit(val recipeId: Long) : Screen()
}