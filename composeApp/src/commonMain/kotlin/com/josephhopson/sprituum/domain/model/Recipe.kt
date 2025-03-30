package com.josephhopson.sprituum.domain.model

import kotlinx.datetime.Instant

/**
 * Domain entity for Recipe
 *
 * This is the core business object that represents a cocktail recipe.
 */
data class Recipe(
    val id: Long = 0,
    val name: String,
    val altName: String? = null,
    val favorite: Boolean = false,
    val imagePath: String? = null,
    val about: String? = null,
    val tags: List<String> = emptyList(),
    val instructions: List<Instruction> = emptyList(),
    val notes: String? = null,
    val alcoholic: Boolean = true,
    val glassware: String? = null,
    val garnish: String? = null,
    val ingredients: List<Ingredient> = emptyList(),
    val createdAt: Instant,
    val updatedAt: Instant
) {
    /**
     * Validates if this recipe contains the minimum required data
     */
    fun isValid(): Boolean {
        return name.isNotBlank() && ingredients.isNotEmpty()
    }
}

/**
 * Domain entity for Instruction
 */
data class Instruction(
    val step: Int,
    val value: String
)

/**
 * Domain entity for Ingredient
 */
data class Ingredient(
    val name: String,
    val amount: Amount? = null,
    val notes: String? = null
)

/**
 * Domain entity for Amount
 */
data class Amount(
    val value: Double,
    val label: String
)
