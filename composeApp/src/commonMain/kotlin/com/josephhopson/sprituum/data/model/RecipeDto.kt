package com.josephhopson.sprituum.data.model

/**
 * Data Transfer Object for Recipe
 *
 * This class represents the Recipe entity as stored in the database.
 */
data class RecipeDto(
    val id: Long = 0,
    val name: String,
    val altName: String? = null,
    val favorite: Boolean = false,
    val imagePath: String? = null,
    val about: String? = null,
    val tags: List<String> = emptyList(),
    val instructions: List<InstructionDto> = emptyList(),
    val notes: String? = null,
    val alcoholic: Boolean = true,
    val glassware: String? = null,
    val garnish: String? = null,
    val ingredients: List<IngredientDto> = emptyList(),
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Data Transfer Object for Instruction
 */
data class InstructionDto(
    val step: Int,
    val value: String
)

/**
 * Data Transfer Object for Ingredient
 */
data class IngredientDto(
    val name: String,
    val amount: AmountDto? = null,
    val notes: String? = null
)

/**
 * Data Transfer Object for Amount
 */
data class AmountDto(
    val value: Double,
    val label: String
)
