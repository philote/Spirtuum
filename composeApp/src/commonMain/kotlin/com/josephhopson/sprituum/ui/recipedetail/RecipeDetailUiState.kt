package com.josephhopson.sprituum.ui.recipedetail

import com.josephhopson.sprituum.domain.model.Recipe

/**
 * UI State for the recipe detail screen
 */
data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * UI Events that can be triggered from the recipe detail screen
 */
sealed class RecipeDetailUiEvent {
    data object ToggleFavorite : RecipeDetailUiEvent()
    data object DeleteRecipe : RecipeDetailUiEvent()
    data object EditRecipe : RecipeDetailUiEvent()
    data object ShareRecipe : RecipeDetailUiEvent()
    data object NavigateBack : RecipeDetailUiEvent()
}

/**
 * Navigation events from the recipe detail screen
 */
sealed class RecipeDetailNavigationEvent {
    data object NavigateBack : RecipeDetailNavigationEvent()
    data class NavigateToEdit(val recipeId: Long) : RecipeDetailNavigationEvent()
    data class ShareRecipe(val recipe: Recipe) : RecipeDetailNavigationEvent()
}