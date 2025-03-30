package com.josephhopson.sprituum.domain.model

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.datetime.Clock

class RecipeTest {

    @Test
    fun testValidRecipeWithNameAndIngredients() {
        val recipe = Recipe(
            name = "Mojito",
            ingredients = listOf(
                Ingredient(name = "White rum"),
                Ingredient(name = "Sugar"),
                Ingredient(name = "Lime juice"),
                Ingredient(name = "Soda water"),
                Ingredient(name = "Mint")
            ),
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )

        assertTrue(recipe.isValid())
    }

    @Test
    fun testInvalidRecipeWithEmptyName() {
        val recipe = Recipe(
            name = "",
            ingredients = listOf(
                Ingredient(name = "White rum")
            ),
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )

        assertFalse(recipe.isValid())
    }

    @Test
    fun testInvalidRecipeWithNoIngredients() {
        val recipe = Recipe(
            name = "Mojito",
            ingredients = emptyList(),
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )

        assertFalse(recipe.isValid())
    }
}
