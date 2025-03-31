package com.josephhopson.sprituum.ui.recipelist

import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.FilterOption
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.SortOption
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.ViewMode

/**
 * UI state for the recipe list screen
 */
data class RecipeListUiState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val sortOption: SortOption = SortOption.NAME_ASC,
    val filterOption: FilterOption = FilterOption.ALL,
    val viewMode: ViewMode = ViewMode.LIST,
    val searchQuery: String = ""
)

/**
 * UI events that can be triggered from the recipe list screen
 */
sealed class RecipeListUiEvent {
    data class SelectRecipe(val recipeId: Long) : RecipeListUiEvent()
    data object CreateNewRecipe : RecipeListUiEvent()
    data class DeleteRecipe(val recipeId: Long) : RecipeListUiEvent()
    data class ToggleFavorite(val recipeId: Long) : RecipeListUiEvent()
    data class UpdateSortOption(val sortOption: SortOption) : RecipeListUiEvent()
    data class UpdateFilterOption(val filterOption: FilterOption) : RecipeListUiEvent()
    data class UpdateViewMode(val viewMode: ViewMode) : RecipeListUiEvent()
    data class UpdateSearchQuery(val query: String) : RecipeListUiEvent()
    data object ClearSearch : RecipeListUiEvent()
}