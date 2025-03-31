package com.josephhopson.sprituum.ui.recipelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.FilterOption
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.SortOption
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.ViewMode
import com.josephhopson.sprituum.domain.usecase.DeleteRecipeUseCase
import com.josephhopson.sprituum.domain.usecase.GetRecipesUseCase
import com.josephhopson.sprituum.domain.usecase.SearchRecipesUseCase
import com.josephhopson.sprituum.domain.usecase.ToggleFavoriteRecipeUseCase
import com.josephhopson.sprituum.domain.usecase.UpdateFilterOptionUseCase
import com.josephhopson.sprituum.domain.usecase.UpdateSortOptionUseCase
import com.josephhopson.sprituum.domain.usecase.UpdateViewModeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the recipe list screen
 */
class RecipeListViewModel(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val searchRecipesUseCase: SearchRecipesUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
    private val toggleFavoriteRecipeUseCase: ToggleFavoriteRecipeUseCase,
    private val updateSortOptionUseCase: UpdateSortOptionUseCase,
    private val updateFilterOptionUseCase: UpdateFilterOptionUseCase,
    private val updateViewModeUseCase: UpdateViewModeUseCase
) : ViewModel() {

    // Internal mutable state
    private val _uiState = MutableStateFlow(RecipeListUiState())

    // Public immutable state exposed to the UI
    val uiState: StateFlow<RecipeListUiState> = _uiState

    // Navigation events
    private val _navigationEvent = MutableStateFlow<Long?>(null)
    val navigationEvent: StateFlow<Long?> = _navigationEvent

    init {
        // Load initial data
        loadRecipes()
    }

    /**
     * Handle UI events from the recipe list screen
     */
    fun onEvent(event: RecipeListUiEvent) {
        when (event) {
            is RecipeListUiEvent.SelectRecipe -> {
                _navigationEvent.value = event.recipeId
            }

            is RecipeListUiEvent.CreateNewRecipe -> {
                _navigationEvent.value = 0L // Special value for creating a new recipe
            }

            is RecipeListUiEvent.DeleteRecipe -> {
                deleteRecipe(event.recipeId)
            }

            is RecipeListUiEvent.ToggleFavorite -> {
                toggleFavorite(event.recipeId)
            }

            is RecipeListUiEvent.UpdateSortOption -> {
                updateSortOption(event.sortOption)
            }

            is RecipeListUiEvent.UpdateFilterOption -> {
                updateFilterOption(event.filterOption)
            }

            is RecipeListUiEvent.UpdateViewMode -> {
                updateViewMode(event.viewMode)
            }

            is RecipeListUiEvent.UpdateSearchQuery -> {
                updateSearchQuery(event.query)
            }

            is RecipeListUiEvent.ClearSearch -> {
                clearSearch()
            }
        }
    }

    /**
     * Clear navigation event after handling
     */
    fun consumeNavigationEvent() {
        _navigationEvent.value = null
    }

    /**
     * Load recipes based on current settings
     */
    private fun loadRecipes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                if (_uiState.value.searchQuery.isBlank()) {
                    getRecipesUseCase()
                        .catch { e ->
                            _uiState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    error = e.message ?: "Failed to load recipes"
                                )
                            }
                        }
                        .collect { recipes ->
                            _uiState.update { state ->
                                state.copy(
                                    recipes = recipes,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                } else {
                    searchRecipesUseCase(_uiState.value.searchQuery)
                        .catch { e ->
                            _uiState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    error = e.message ?: "Failed to search recipes"
                                )
                            }
                        }
                        .collect { recipes ->
                            _uiState.update { state ->
                                state.copy(
                                    recipes = recipes,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = e.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }

    /**
     * Delete a recipe by ID
     */
    private fun deleteRecipe(recipeId: Long) {
        viewModelScope.launch {
            try {
                val success = deleteRecipeUseCase(recipeId)
                if (!success) {
                    _uiState.update { state ->
                        state.copy(error = "Failed to delete recipe")
                    }
                }
                // Recipe list will be updated automatically through the Flow
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(error = e.message ?: "Failed to delete recipe")
                }
            }
        }
    }

    /**
     * Toggle favorite status for a recipe
     */
    private fun toggleFavorite(recipeId: Long) {
        viewModelScope.launch {
            try {
                val success = toggleFavoriteRecipeUseCase(recipeId)
                if (!success) {
                    _uiState.update { state ->
                        state.copy(error = "Failed to update favorite status")
                    }
                }
                // Recipe list will be updated automatically through the Flow
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(error = e.message ?: "Failed to update favorite status")
                }
            }
        }
    }

    /**
     * Update sort option
     */
    private fun updateSortOption(sortOption: SortOption) {
        viewModelScope.launch {
            try {
                updateSortOptionUseCase(sortOption)
                _uiState.update { state ->
                    state.copy(sortOption = sortOption)
                }
                // Recipe list will be updated automatically through the Flow
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(error = e.message ?: "Failed to update sort option")
                }
            }
        }
    }

    /**
     * Update filter option
     */
    private fun updateFilterOption(filterOption: FilterOption) {
        viewModelScope.launch {
            try {
                updateFilterOptionUseCase(filterOption)
                _uiState.update { state ->
                    state.copy(filterOption = filterOption)
                }
                // Recipe list will be updated automatically through the Flow
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(error = e.message ?: "Failed to update filter option")
                }
            }
        }
    }

    /**
     * Update view mode
     */
    private fun updateViewMode(viewMode: ViewMode) {
        viewModelScope.launch {
            try {
                updateViewModeUseCase(viewMode)
                _uiState.update { state ->
                    state.copy(viewMode = viewMode)
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(error = e.message ?: "Failed to update view mode")
                }
            }
        }
    }

    /**
     * Update search query
     */
    private fun updateSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(searchQuery = query)
        }
        loadRecipes()
    }

    /**
     * Clear search query
     */
    private fun clearSearch() {
        _uiState.update { state ->
            state.copy(searchQuery = "")
        }
        loadRecipes()
    }
}