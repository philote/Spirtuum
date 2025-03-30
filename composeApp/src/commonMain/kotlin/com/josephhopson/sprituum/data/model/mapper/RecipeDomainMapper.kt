package com.josephhopson.sprituum.data.model.mapper

import com.josephhopson.sprituum.data.model.AmountDto
import com.josephhopson.sprituum.data.model.IngredientDto
import com.josephhopson.sprituum.data.model.InstructionDto
import com.josephhopson.sprituum.data.model.RecipeDto
import com.josephhopson.sprituum.domain.model.Amount
import com.josephhopson.sprituum.domain.model.Ingredient
import com.josephhopson.sprituum.domain.model.Instruction
import com.josephhopson.sprituum.domain.model.Recipe
import kotlinx.datetime.Instant

/**
 * Extension function to convert a RecipeDto to a domain Recipe entity
 */
fun RecipeDto.toRecipe(): Recipe = Recipe(
    id = id,
    name = name,
    altName = altName,
    favorite = favorite,
    imagePath = imagePath,
    about = about,
    tags = tags,
    instructions = instructions.map { it.toInstruction() },
    notes = notes,
    alcoholic = alcoholic,
    glassware = glassware,
    garnish = garnish,
    ingredients = ingredients.map { it.toIngredient() },
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    updatedAt = Instant.fromEpochMilliseconds(updatedAt)
)

/**
 * Extension function to convert a domain Recipe entity to a RecipeDto
 */
fun Recipe.toDto(): RecipeDto = RecipeDto(
    id = id,
    name = name,
    altName = altName,
    favorite = favorite,
    imagePath = imagePath,
    about = about,
    tags = tags,
    instructions = instructions.map { it.toDto() },
    notes = notes,
    alcoholic = alcoholic,
    glassware = glassware,
    garnish = garnish,
    ingredients = ingredients.map { it.toDto() },
    createdAt = createdAt.toEpochMilliseconds(),
    updatedAt = updatedAt.toEpochMilliseconds()
)

/**
 * Extension function to convert an InstructionDto to a domain Instruction entity
 */
fun InstructionDto.toInstruction(): Instruction = Instruction(
    step = step,
    value = value
)

/**
 * Extension function to convert a domain Instruction entity to an InstructionDto
 */
fun Instruction.toDto(): InstructionDto = InstructionDto(
    step = step,
    value = value
)

/**
 * Extension function to convert an IngredientDto to a domain Ingredient entity
 */
fun IngredientDto.toIngredient(): Ingredient = Ingredient(
    name = name,
    amount = amount?.toAmount(),
    notes = notes
)

/**
 * Extension function to convert a domain Ingredient entity to an IngredientDto
 */
fun Ingredient.toDto(): IngredientDto = IngredientDto(
    name = name,
    amount = amount?.toDto(),
    notes = notes
)

/**
 * Extension function to convert an AmountDto to a domain Amount entity
 */
fun AmountDto.toAmount(): Amount = Amount(
    value = value,
    label = label
)

/**
 * Extension function to convert a domain Amount entity to an AmountDto
 */
fun Amount.toDto(): AmountDto = AmountDto(
    value = value,
    label = label
)
