package com.josephhopson.sprituum.ui.recipeedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josephhopson.sprituum.domain.usecase.GetRecipeByIdUseCase
import com.josephhopson.sprituum.domain.usecase.SaveRecipeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * ViewModel for the recipe edit screen
 */
class RecipeEditViewModel(
    private val recipeId: Long,
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase,
    private val saveRecipeUseCase: SaveRecipeUseCase
) : ViewModel() {

    // Internal mutable state
    private val _uiState = MutableStateFlow(RecipeEditUiState(isNewRecipe = recipeId <= 0))

    // Public immutable state exposed to the UI
    val uiState: StateFlow<RecipeEditUiState> = _uiState.asStateFlow()

    // Navigation events
    private val _navigationEvent = MutableStateFlow<RecipeEditNavigationEvent?>(null)
    val navigationEvent: StateFlow<RecipeEditNavigationEvent?> = _navigationEvent.asStateFlow()

    init {
        if (recipeId > 0) {
            // Existing recipe - load it
            loadRecipe()
        }
    }

    /**
     * Handle UI events
     */
    fun onEvent(event: RecipeEditUiEvent) {
        when (event) {
            // Basic fields
            is RecipeEditUiEvent.UpdateName -> updateName(event.name)
            is RecipeEditUiEvent.UpdateAltName -> updateAltName(event.altName)
            is RecipeEditUiEvent.UpdateAbout -> updateAbout(event.about)
            is RecipeEditUiEvent.UpdateNotes -> updateNotes(event.notes)
            is RecipeEditUiEvent.UpdateGlassware -> updateGlassware(event.glassware)
            is RecipeEditUiEvent.UpdateGarnish -> updateGarnish(event.garnish)
            is RecipeEditUiEvent.ToggleAlcoholic -> toggleAlcoholic()
            is RecipeEditUiEvent.ToggleFavorite -> toggleFavorite()

            // Tag management
            is RecipeEditUiEvent.UpdateNewTag -> updateNewTag(event.tag)
            is RecipeEditUiEvent.AddTag -> addTag()
            is RecipeEditUiEvent.DeleteTag -> deleteTag(event.index)

            // Ingredient management
            is RecipeEditUiEvent.AddIngredient -> addIngredient(event.name)
            is RecipeEditUiEvent.UpdateIngredientName -> updateIngredientName(event.index, event.name)
            is RecipeEditUiEvent.UpdateIngredientAmount -> updateIngredientAmount(event.index, event.amount)
            is RecipeEditUiEvent.UpdateIngredientUnit -> updateIngredientUnit(event.index, event.unit)
            is RecipeEditUiEvent.UpdateIngredientNotes -> updateIngredientNotes(event.index, event.notes)
            is RecipeEditUiEvent.DeleteIngredient -> deleteIngredient(event.index)
            is RecipeEditUiEvent.MoveIngredientUp -> moveIngredientUp(event.index)
            is RecipeEditUiEvent.MoveIngredientDown -> moveIngredientDown(event.index)

            // Instruction management
            is RecipeEditUiEvent.AddInstruction -> addInstruction(event.value)
            is RecipeEditUiEvent.UpdateInstructionValue -> updateInstructionValue(event.index, event.value)
            is RecipeEditUiEvent.DeleteInstruction -> deleteInstruction(event.index)
            is RecipeEditUiEvent.MoveInstructionUp -> moveInstructionUp(event.index)
            is RecipeEditUiEvent.MoveInstructionDown -> moveInstructionDown(event.index)

            // Image management
            RecipeEditUiEvent.TakePhoto -> takePhoto()
            RecipeEditUiEvent.RemovePhoto -> removePhoto()

            // Actions
            RecipeEditUiEvent.SaveRecipe -> saveRecipe()
            RecipeEditUiEvent.Cancel -> cancel()
        }
    }

    /**
     * Clear navigation event after handling
     */
    fun consumeNavigationEvent() {
        _navigationEvent.value = null
    }

    /**
     * Set an image path after photo capture
     */
    fun setImagePath(path: String?) {
        _uiState.update { it.copy(imagePath = path) }
    }

    /**
     * Load an existing recipe
     */
    private fun loadRecipe() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val recipe = getRecipeByIdUseCase(recipeId)
                if (recipe != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isNewRecipe = false,
                            id = recipe.id,
                            name = recipe.name,
                            altName = recipe.altName ?: "",
                            favorite = recipe.favorite,
                            imagePath = recipe.imagePath,
                            about = recipe.about ?: "",
                            tags = recipe.tags,
                            instructions = recipe.instructions.mapIndexed { index, instruction ->
                                InstructionUiState(
                                    index = index,
                                    step = instruction.step,
                                    value = instruction.value
                                )
                            },
                            notes = recipe.notes ?: "",
                            alcoholic = recipe.alcoholic,
                            glassware = recipe.glassware ?: "",
                            garnish = recipe.garnish ?: "",
                            ingredients = recipe.ingredients.mapIndexed { index, ingredient ->
                                IngredientUiState(
                                    index = index,
                                    name = ingredient.name,
                                    amount = ingredient.amount?.value,
                                    unit = ingredient.amount?.label ?: "",
                                    notes = ingredient.notes ?: ""
                                )
                            },
                            createdAt = recipe.createdAt,
                            updatedAt = recipe.updatedAt
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
     * Save the recipe
     */
    private fun saveRecipe() {
        val recipeState = _uiState.value
        if (!recipeState.isValid()) {
            val validationErrors = mutableMapOf<String, String>()
            if (recipeState.name.isBlank()) {
                validationErrors["name"] = "Name is required"
            }
            if (recipeState.ingredients.isEmpty()) {
                validationErrors["ingredients"] = "At least one ingredient is required"
            }
            _uiState.update { it.copy(validationErrors = validationErrors) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, validationErrors = emptyMap()) }

            val now = Clock.System.now()
            val recipe = recipeState.toRecipe().copy(
                updatedAt = now,
                createdAt = recipeState.createdAt ?: now
            )

            when (val result = saveRecipeUseCase(recipe)) {
                is SaveRecipeUseCase.Result.Success -> {
                    _navigationEvent.value = RecipeEditNavigationEvent.NavigateToDetail(result.id)
                }

                SaveRecipeUseCase.Result.InvalidRecipe -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            error = "Invalid recipe. Please check all fields."
                        )
                    }
                }

                SaveRecipeUseCase.Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            error = "Failed to save recipe. Please try again."
                        )
                    }
                }
            }
        }
    }

    /**
     * Cancel editing and go back
     */
    private fun cancel() {
        _navigationEvent.value = RecipeEditNavigationEvent.NavigateBack
    }

    /**
     * Basic field updates
     */
    private fun updateName(name: String) {
        _uiState.update {
            it.copy(
                name = name,
                validationErrors = it.validationErrors - "name"
            )
        }
    }

    private fun updateAltName(altName: String) {
        _uiState.update { it.copy(altName = altName) }
    }

    private fun updateAbout(about: String) {
        _uiState.update { it.copy(about = about) }
    }

    private fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    private fun updateGlassware(glassware: String) {
        _uiState.update { it.copy(glassware = glassware) }
    }

    private fun updateGarnish(garnish: String) {
        _uiState.update { it.copy(garnish = garnish) }
    }

    private fun toggleAlcoholic() {
        _uiState.update { it.copy(alcoholic = !it.alcoholic) }
    }

    private fun toggleFavorite() {
        _uiState.update { it.copy(favorite = !it.favorite) }
    }

    /**
     * Tag management
     */
    private fun updateNewTag(tag: String) {
        _uiState.update { it.copy(newTag = tag) }
    }

    private fun addTag() {
        val tag = _uiState.value.newTag.trim()
        if (tag.isBlank() || _uiState.value.tags.contains(tag)) {
            return
        }

        _uiState.update {
            it.copy(
                tags = it.tags + tag,
                newTag = ""
            )
        }
    }

    private fun deleteTag(index: Int) {
        _uiState.update {
            it.copy(
                tags = it.tags.filterIndexed { i, _ -> i != index }
            )
        }
    }

    /**
     * Ingredient management
     */
    private fun addIngredient(name: String) {
        val ingredients = _uiState.value.ingredients.toMutableList()
        ingredients.add(
            IngredientUiState(
                index = ingredients.size,
                name = name
            )
        )

        _uiState.update {
            it.copy(
                ingredients = ingredients,
                validationErrors = it.validationErrors - "ingredients"
            )
        }
    }

    private fun updateIngredientName(index: Int, name: String) {
        updateIngredient(index) { it.copy(name = name) }
    }

    private fun updateIngredientAmount(index: Int, amount: Double?) {
        updateIngredient(index) { it.copy(amount = amount) }
    }

    private fun updateIngredientUnit(index: Int, unit: String) {
        updateIngredient(index) { it.copy(unit = unit) }
    }

    private fun updateIngredientNotes(index: Int, notes: String) {
        updateIngredient(index) { it.copy(notes = notes) }
    }

    private fun deleteIngredient(index: Int) {
        val ingredients = _uiState.value.ingredients.filterIndexed { i, _ -> i != index }
            .mapIndexed { i, ingredient -> ingredient.copy(index = i) }

        _uiState.update {
            it.copy(
                ingredients = ingredients,
                validationErrors = if (ingredients.isEmpty()) {
                    it.validationErrors + ("ingredients" to "At least one ingredient is required")
                } else {
                    it.validationErrors - "ingredients"
                }
            )
        }
    }

    private fun moveIngredientUp(index: Int) {
        if (index <= 0 || index >= _uiState.value.ingredients.size) {
            return
        }

        val ingredients = _uiState.value.ingredients.toMutableList()
        val temp = ingredients[index]
        ingredients[index] = ingredients[index - 1].copy(index = index)
        ingredients[index - 1] = temp.copy(index = index - 1)

        _uiState.update { it.copy(ingredients = ingredients) }
    }

    private fun moveIngredientDown(index: Int) {
        if (index < 0 || index >= _uiState.value.ingredients.size - 1) {
            return
        }

        val ingredients = _uiState.value.ingredients.toMutableList()
        val temp = ingredients[index]
        ingredients[index] = ingredients[index + 1].copy(index = index)
        ingredients[index + 1] = temp.copy(index = index + 1)

        _uiState.update { it.copy(ingredients = ingredients) }
    }

    private fun updateIngredient(index: Int, update: (IngredientUiState) -> IngredientUiState) {
        val ingredients = _uiState.value.ingredients.toMutableList()
        if (index in ingredients.indices) {
            ingredients[index] = update(ingredients[index])
            _uiState.update { it.copy(ingredients = ingredients) }
        }
    }

    /**
     * Instruction management
     */
    private fun addInstruction(value: String) {
        val instructions = _uiState.value.instructions.toMutableList()
        val step = if (instructions.isEmpty()) 1 else instructions.maxOf { it.step } + 1

        instructions.add(
            InstructionUiState(
                index = instructions.size,
                step = step,
                value = value
            )
        )

        _uiState.update { it.copy(instructions = instructions) }
    }

    private fun updateInstructionValue(index: Int, value: String) {
        updateInstruction(index) { it.copy(value = value) }
    }

    private fun deleteInstruction(index: Int) {
        val instructions = _uiState.value.instructions.filterIndexed { i, _ -> i != index }
            .mapIndexed { i, instruction -> instruction.copy(index = i) }

        // Renumber steps if needed
        val renumberedInstructions = if (instructions.isNotEmpty()) {
            instructions.mapIndexed { i, instruction ->
                instruction.copy(step = i + 1)
            }
        } else {
            instructions
        }

        _uiState.update { it.copy(instructions = renumberedInstructions) }
    }

    private fun moveInstructionUp(index: Int) {
        if (index <= 0 || index >= _uiState.value.instructions.size) {
            return
        }

        val instructions = _uiState.value.instructions.toMutableList()
        val temp = instructions[index]
        instructions[index] = instructions[index - 1].copy(
            index = index,
            step = temp.step
        )
        instructions[index - 1] = temp.copy(
            index = index - 1,
            step = instructions[index].step
        )

        _uiState.update { it.copy(instructions = instructions) }
    }

    private fun moveInstructionDown(index: Int) {
        if (index < 0 || index >= _uiState.value.instructions.size - 1) {
            return
        }

        val instructions = _uiState.value.instructions.toMutableList()
        val temp = instructions[index]
        instructions[index] = instructions[index + 1].copy(
            index = index,
            step = temp.step
        )
        instructions[index + 1] = temp.copy(
            index = index + 1,
            step = instructions[index].step
        )

        _uiState.update { it.copy(instructions = instructions) }
    }

    private fun updateInstruction(index: Int, update: (InstructionUiState) -> InstructionUiState) {
        val instructions = _uiState.value.instructions.toMutableList()
        if (index in instructions.indices) {
            instructions[index] = update(instructions[index])
            _uiState.update { it.copy(instructions = instructions) }
        }
    }

    /**
     * Image management
     */
    private fun takePhoto() {
        _navigationEvent.value = RecipeEditNavigationEvent.CapturePhoto
    }

    private fun removePhoto() {
        _uiState.update { it.copy(imagePath = null) }
    }
}