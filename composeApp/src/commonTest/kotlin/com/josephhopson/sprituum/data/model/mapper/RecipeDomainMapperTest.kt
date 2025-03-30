package com.josephhopson.sprituum.data.model.mapper

import com.josephhopson.sprituum.data.model.AmountDto
import com.josephhopson.sprituum.data.model.IngredientDto
import com.josephhopson.sprituum.data.model.InstructionDto
import com.josephhopson.sprituum.data.model.RecipeDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.Instant

class RecipeDomainMapperTest {

    @Test
    fun testRecipeDtoToDomainMapping() {
        // Given
        val recipeDto = RecipeDto(
            id = 1,
            name = "Mojito",
            altName = "Cuban Mojito",
            favorite = true,
            imagePath = "path/to/image.jpg",
            about = "A refreshing Cuban highball",
            tags = listOf("Cuban", "Refreshing", "Summer"),
            instructions = listOf(
                InstructionDto(step = 1, value = "Muddle mint leaves with sugar and lime juice"),
                InstructionDto(step = 2, value = "Add ice and rum"),
                InstructionDto(step = 3, value = "Top with soda water")
            ),
            notes = "Best served with crushed ice",
            alcoholic = true,
            glassware = "Highball glass",
            garnish = "Mint sprig and lime wheel",
            ingredients = listOf(
                IngredientDto(
                    name = "White rum",
                    amount = AmountDto(value = 60.0, label = "ml"),
                    notes = "Preferably Cuban rum"
                ),
                IngredientDto(
                    name = "Fresh lime juice",
                    amount = AmountDto(value = 30.0, label = "ml"),
                    notes = null
                )
            ),
            createdAt = 1648166400000, // 2022-03-25
            updatedAt = 1648166400000
        )

        // When
        val recipe = recipeDto.toRecipe()

        // Then
        assertEquals(1L, recipe.id)
        assertEquals("Mojito", recipe.name)
        assertEquals("Cuban Mojito", recipe.altName)
        assertEquals(true, recipe.favorite)
        assertEquals("path/to/image.jpg", recipe.imagePath)
        assertEquals("A refreshing Cuban highball", recipe.about)
        assertEquals(true, recipe.alcoholic)
        assertEquals("Highball glass", recipe.glassware)
        assertEquals("Mint sprig and lime wheel", recipe.garnish)
        assertEquals("Best served with crushed ice", recipe.notes)
        assertEquals(Instant.fromEpochMilliseconds(1648166400000), recipe.createdAt)
        assertEquals(Instant.fromEpochMilliseconds(1648166400000), recipe.updatedAt)

        // Check tags
        assertEquals(3, recipe.tags.size)
        assertEquals(listOf("Cuban", "Refreshing", "Summer"), recipe.tags)

        // Check instructions
        assertEquals(3, recipe.instructions.size)
        assertEquals(1, recipe.instructions[0].step)
        assertEquals("Muddle mint leaves with sugar and lime juice", recipe.instructions[0].value)
        assertEquals(2, recipe.instructions[1].step)
        assertEquals("Add ice and rum", recipe.instructions[1].value)

        // Check ingredients
        assertEquals(2, recipe.ingredients.size)
        assertEquals("White rum", recipe.ingredients[0].name)
        assertEquals(60.0, recipe.ingredients[0].amount?.value)
        assertEquals("ml", recipe.ingredients[0].amount?.label)
        assertEquals("Preferably Cuban rum", recipe.ingredients[0].notes)
        assertEquals(null, recipe.ingredients[1].notes)
    }
}
