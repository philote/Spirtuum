package com.josephhopson.sprituum.ui.recipedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josephhopson.sprituum.domain.usecase.DeleteRecipeUseCase
import com.josephhopson.sprituum.domain.usecase.GetRecipeByIdUseCase
import com.josephhopson.sprituum.domain.usecase.ToggleFavoriteRecipeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the recipe detail screen
 */
class RecipeDetailViewModel(
    private val recipeId: Long,
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase,
    private val toggleFavoriteRecipeUseCase: ToggleFavoriteRecipeUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase
) : ViewModel() {

    // Internal mutable state
    private val _uiState = MutableStateFlow(RecipeDetailUiState())

    // Public immutable state exposed to the UI
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    // Navigation events
    private val _navigationEvent = MutableStateFlow<RecipeDetailNavigationEvent?>(null)
    val navigationEvent: StateFlow<RecipeDetailNavigationEvent?> = _navigationEvent.asStateFlow()

    init {
        loadRecipe()
    }

    /**
     * Handle UI events from the recipe detail screen
     */
    fun onEvent(event: RecipeDetailUiEvent) {
        when (event) {
            is RecipeDetailUiEvent.ToggleFavorite -> toggleFavorite()
            is RecipeDetailUiEvent.DeleteRecipe -> deleteRecipe()
            is RecipeDetailUiEvent.EditRecipe -> navigateToEditRecipe()
            is RecipeDetailUiEvent.ShareRecipe -> shareRecipe()
            is RecipeDetailUiEvent.NavigateBack -> navigateBack()
        }
    }

    /**
     * Clear navigation event after handling
     */
    fun consumeNavigationEvent() {
        _navigationEvent.value = null
    }

    /**
     * Load recipe details by ID
     */
    private fun loadRecipe() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val recipe = getRecipeByIdUseCase(recipeId)
                if (recipe != null) {
                    _uiState.update {
                        it.copy(
                            recipe = recipe,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Recipe not found"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    /**
     * Toggle favorite status of the recipe
     */
    private fun toggleFavorite() {
        viewModelScope.launch {
            try {
                _uiState.value.recipe?.let { recipe ->
                    val success = toggleFavoriteRecipeUseCase(recipe.id)
                    if (success) {
                        // Reload the recipe to get updated data
                        loadRecipe()
                    } else {
                        _uiState.update {
                            it.copy(error = "Failed to update favorite status")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Failed to update favorite status")
                }
            }
        }
    }

    /**
     * Delete the current recipe
     */
    private fun deleteRecipe() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                _uiState.value.recipe?.let { recipe ->
                    val success = deleteRecipeUseCase(recipe.id)
                    if (success) {
                        _navigationEvent.value = RecipeDetailNavigationEvent.NavigateBack
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false, error = "Failed to delete recipe")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to delete recipe")
                }
            }
        }
    }

    /**
     * Navigate to edit recipe screen
     */
    private fun navigateToEditRecipe() {
        _uiState.value.recipe?.let { recipe ->
            _navigationEvent.value = RecipeDetailNavigationEvent.NavigateToEdit(recipe.id)
        }
    }

    /**
     * Share recipe as plain text
     */
    private fun shareRecipe() {
        _uiState.value.recipe?.let { recipe ->
            _navigationEvent.value = RecipeDetailNavigationEvent.ShareRecipe(recipe)
        }
    }

    /**
     * Navigate back to recipe list
     */
    private fun navigateBack() {
        _navigationEvent.value = RecipeDetailNavigationEvent.NavigateBack
    }
}