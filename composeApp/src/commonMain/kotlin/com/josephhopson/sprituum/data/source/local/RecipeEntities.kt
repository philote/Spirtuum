package com.josephhopson.sprituum.data.source.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.josephhopson.sprituum.data.model.AmountDto
import com.josephhopson.sprituum.data.model.IngredientDto
import com.josephhopson.sprituum.data.model.InstructionDto
import com.josephhopson.sprituum.data.model.RecipeDto

/**
 * Room entity for Recipe
 */
@Entity(
    tableName = "recipes"
)
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val altName: String? = null,
    val favorite: Boolean = false,
    val imagePath: String? = null,
    val about: String? = null,
    val alcoholic: Boolean = true,
    val glassware: String? = null,
    val garnish: String? = null,
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long
) {
    /**
     * Convert this entity to a DTO with related entities
     */
    fun toRecipeDto(
        tags: List<RecipeTagEntity>,
        instructions: List<RecipeInstructionEntity>,
        ingredients: List<RecipeIngredientEntity>
    ): RecipeDto {
        return RecipeDto(
            id = id,
            name = name,
            altName = altName,
            favorite = favorite,
            imagePath = imagePath,
            about = about,
            tags = tags.map { it.tag },
            instructions = instructions.sortedBy { it.step }.map {
                InstructionDto(step = it.step, value = it.value)
            },
            notes = notes,
            alcoholic = alcoholic,
            glassware = glassware,
            garnish = garnish,
            ingredients = ingredients.map {
                IngredientDto(
                    name = it.name,
                    amount = if (it.amountValue != null && it.amountLabel != null) {
                        AmountDto(value = it.amountValue, label = it.amountLabel)
                    } else null,
                    notes = it.notes
                )
            },
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}

/**
 * Room entity for Recipe Tags
 */
@Entity(
    tableName = "recipe_tags",
    indices = [Index("recipeId")]
)
data class RecipeTagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeId: Long,
    val tag: String
)

/**
 * Room entity for Recipe Instructions
 */
@Entity(
    tableName = "recipe_instructions",
    indices = [Index("recipeId")]
)
data class RecipeInstructionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeId: Long,
    val step: Int,
    val value: String
)

/**
 * Room entity for Recipe Ingredients
 */
@Entity(
    tableName = "recipe_ingredients",
    indices = [Index("recipeId")]
)
data class RecipeIngredientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeId: Long,
    val name: String,
    val amountValue: Double? = null,
    val amountLabel: String? = null,
    val notes: String? = null
)
