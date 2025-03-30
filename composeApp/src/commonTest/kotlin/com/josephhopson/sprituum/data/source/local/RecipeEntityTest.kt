package com.josephhopson.sprituum.data.source.local

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test for the RecipeEntity class
 *
 * This is more of an integration test that verifies the entity mapping functionality
 * without needing Room's implementation, which would be platform-specific.
 */
class RecipeEntityTest {

    @Test
    fun testRecipeEntityToDtoConversion() {
        // Given
        val recipeEntity = RecipeEntity(
            id = 1,
            name = "Mojito",
            altName = "Cuban Mojito",
            favorite = true,
            imagePath = "path/to/image.jpg",
            about = "A refreshing Cuban highball",
            alcoholic = true,
            glassware = "Highball glass",
            garnish = "Mint sprig and lime wheel",
            notes = "Best served with crushed ice",
            createdAt = 1648166400000, // 2022-03-25
            updatedAt = 1648166400000
        )

        val tags = listOf(
            RecipeTagEntity(recipeId = 1, tag = "Cuban"),
            RecipeTagEntity(recipeId = 1, tag = "Refreshing"),
            RecipeTagEntity(recipeId = 1, tag = "Summer")
        )

        val instructions = listOf(
            RecipeInstructionEntity(
                recipeId = 1,
                step = 1,
                value = "Muddle mint leaves with sugar and lime juice"
            ),
            RecipeInstructionEntity(recipeId = 1, step = 2, value = "Add ice and rum"),
            RecipeInstructionEntity(recipeId = 1, step = 3, value = "Top with soda water")
        )

        val ingredients = listOf(
            RecipeIngredientEntity(
                recipeId = 1,
                name = "White rum",
                amountValue = 60.0,
                amountLabel = "ml",
                notes = "Preferably Cuban rum"
            ),
            RecipeIngredientEntity(
                recipeId = 1,
                name = "Fresh lime juice",
                amountValue = 30.0,
                amountLabel = "ml",
                notes = null
            )
        )

        // When
        val recipeDto = recipeEntity.toRecipeDto(tags, instructions, ingredients)

        // Then
        assertEquals(1L, recipeDto.id)
        assertEquals("Mojito", recipeDto.name)
        assertEquals("Cuban Mojito", recipeDto.altName)
        assertEquals(true, recipeDto.favorite)
        assertEquals("path/to/image.jpg", recipeDto.imagePath)
        assertEquals("A refreshing Cuban highball", recipeDto.about)
        assertEquals(true, recipeDto.alcoholic)
        assertEquals("Highball glass", recipeDto.glassware)
        assertEquals("Mint sprig and lime wheel", recipeDto.garnish)
        assertEquals("Best served with crushed ice", recipeDto.notes)
        assertEquals(1648166400000, recipeDto.createdAt)
        assertEquals(1648166400000, recipeDto.updatedAt)

        // Check tags
        assertEquals(3, recipeDto.tags.size)
        assertEquals(listOf("Cuban", "Refreshing", "Summer"), recipeDto.tags)

        // Check instructions
        assertEquals(3, recipeDto.instructions.size)
        assertEquals(1, recipeDto.instructions[0].step)
        assertEquals(
            "Muddle mint leaves with sugar and lime juice",
            recipeDto.instructions[0].value
        )
        assertEquals(2, recipeDto.instructions[1].step)
        assertEquals("Add ice and rum", recipeDto.instructions[1].value)

        // Check ingredients
        assertEquals(2, recipeDto.ingredients.size)
        assertEquals("White rum", recipeDto.ingredients[0].name)
        assertEquals(60.0, recipeDto.ingredients[0].amount?.value)
        assertEquals("ml", recipeDto.ingredients[0].amount?.label)
        assertEquals("Preferably Cuban rum", recipeDto.ingredients[0].notes)
        assertEquals(null, recipeDto.ingredients[1].notes)
    }
}
