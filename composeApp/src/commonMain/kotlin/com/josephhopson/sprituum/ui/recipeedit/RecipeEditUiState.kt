package com.josephhopson.sprituum.ui.recipeedit

import com.josephhopson.sprituum.domain.model.Amount
import com.josephhopson.sprituum.domain.model.Ingredient
import com.josephhopson.sprituum.domain.model.Instruction
import com.josephhopson.sprituum.domain.model.Recipe
import kotlinx.datetime.Instant

/**
 * UI state for the recipe edit screen
 */
data class RecipeEditUiState(
    val isLoading: Boolean = false,
    val isNewRecipe: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val validationErrors: Map<String, String> = emptyMap(),

    // Recipe fields
    val id: Long = 0,
    val name: String = "",
    val altName: String = "",
    val favorite: Boolean = false,
    val imagePath: String? = null,
    val about: String = "",
    val tags: List<String> = emptyList(),
    val instructions: List<InstructionUiState> = emptyList(),
    val notes: String = "",
    val alcoholic: Boolean = true,
    val glassware: String = "",
    val garnish: String = "",
    val ingredients: List<IngredientUiState> = emptyList(),

    // New tag input
    val newTag: String = "",

    // Timestamps
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
) {
    /**
     * Checks if the current state represents a valid recipe that can be saved
     */
    fun isValid(): Boolean {
        return name.isNotBlank() && ingredients.isNotEmpty()
    }

    /**
     * Convert UI state to a domain Recipe model
     */
    fun toRecipe(): Recipe {
        return Recipe(
            id = id,
            name = name,
            altName = altName.ifBlank { null },
            favorite = favorite,
            imagePath = imagePath,
            about = about.ifBlank { null },
            tags = tags,
            instructions = instructions.map { it.toInstruction() },
            notes = notes.ifBlank { null },
            alcoholic = alcoholic,
            glassware = glassware.ifBlank { null },
            garnish = garnish.ifBlank { null },
            ingredients = ingredients.map { it.toIngredient() },
            createdAt = createdAt ?: Instant.DISTANT_PAST,
            updatedAt = updatedAt ?: Instant.DISTANT_PAST
        )
    }
}

/**
 * UI state for ingredient
 */
data class IngredientUiState(
    val index: Int,
    val name: String,
    val amount: Double? = null,
    val unit: String = "",
    val notes: String = ""
) {
    fun toIngredient(): Ingredient {
        val amountObj = if (amount != null && unit.isNotBlank()) {
            Amount(amount, unit)
        } else {
            null
        }

        return Ingredient(
            name = name,
            amount = amountObj,
            notes = notes.ifBlank { null }
        )
    }
}

/**
 * UI state for instruction
 */
data class InstructionUiState(
    val index: Int,
    val step: Int,
    val value: String
) {
    fun toInstruction(): Instruction {
        return Instruction(
            step = step,
            value = value
        )
    }
}

/**
 * UI events for the recipe edit screen
 */
sealed class RecipeEditUiEvent {
    data class UpdateName(val name: String) : RecipeEditUiEvent()
    data class UpdateAltName(val altName: String) : RecipeEditUiEvent()
    data class UpdateAbout(val about: String) : RecipeEditUiEvent()
    data class UpdateNotes(val notes: String) : RecipeEditUiEvent()
    data class UpdateGlassware(val glassware: String) : RecipeEditUiEvent()
    data class UpdateGarnish(val garnish: String) : RecipeEditUiEvent()
    data object ToggleAlcoholic : RecipeEditUiEvent()
    data object ToggleFavorite : RecipeEditUiEvent()

    // Tag management
    data class UpdateNewTag(val tag: String) : RecipeEditUiEvent()
    data object AddTag : RecipeEditUiEvent()
    data class DeleteTag(val index: Int) : RecipeEditUiEvent()

    // Ingredient management
    data class AddIngredient(val name: String = "") : RecipeEditUiEvent()
    data class UpdateIngredientName(val index: Int, val name: String) : RecipeEditUiEvent()
    data class UpdateIngredientAmount(val index: Int, val amount: Double?) : RecipeEditUiEvent()
    data class UpdateIngredientUnit(val index: Int, val unit: String) : RecipeEditUiEvent()
    data class UpdateIngredientNotes(val index: Int, val notes: String) : RecipeEditUiEvent()
    data class DeleteIngredient(val index: Int) : RecipeEditUiEvent()
    data class MoveIngredientUp(val index: Int) : RecipeEditUiEvent()
    data class MoveIngredientDown(val index: Int) : RecipeEditUiEvent()

    // Instruction management
    data class AddInstruction(val value: String = "") : RecipeEditUiEvent()
    data class UpdateInstructionValue(val index: Int, val value: String) : RecipeEditUiEvent()
    data class DeleteInstruction(val index: Int) : RecipeEditUiEvent()
    data class MoveInstructionUp(val index: Int) : RecipeEditUiEvent()
    data class MoveInstructionDown(val index: Int) : RecipeEditUiEvent()

    // Image
    data object TakePhoto : RecipeEditUiEvent()
    data object RemovePhoto : RecipeEditUiEvent()

    // Actions
    data object SaveRecipe : RecipeEditUiEvent()
    data object Cancel : RecipeEditUiEvent()
}

/**
 * Navigation events for the recipe edit screen
 */
sealed class RecipeEditNavigationEvent {
    data object NavigateBack : RecipeEditNavigationEvent()
    data class NavigateToDetail(val recipeId: Long) : RecipeEditNavigationEvent()
    data object CapturePhoto : RecipeEditNavigationEvent()
}