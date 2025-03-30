package com.josephhopson.sprituum.data.repository

import com.josephhopson.sprituum.domain.model.Ingredient
import com.josephhopson.sprituum.domain.model.Recipe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for the FakeRecipeRepository implementation
 *
 * These tests validate the behavior of our fake repository, which will be used
 * in tests for other components that depend on the repository.
 */
class FakeRecipeRepositoryTest {

    private fun createSampleRecipe(name: String) = Recipe(
        name = name,
        ingredients = listOf(Ingredient(name = "Test Ingredient")),
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now()
    )

    @Test
    fun testSaveRecipeAssignsId() = runTest {
        // Given
        val repository = FakeRecipeRepository()
        val recipe = createSampleRecipe("Test Recipe")

        // When
        val id = repository.saveRecipe(recipe)

        // Then
        assertTrue(id > 0)
        val savedRecipe = repository.getRecipeById(id)
        assertNotNull(savedRecipe)
        assertEquals("Test Recipe", savedRecipe.name)
    }

    @Test
    fun testGetRecipesReturnsAll() = runTest {
        // Given
        val repository = FakeRecipeRepository()
        val recipe1 = createSampleRecipe("Recipe 1")
        val recipe2 = createSampleRecipe("Recipe 2")

        // When
        val id1 = repository.saveRecipe(recipe1)
        val id2 = repository.saveRecipe(recipe2)

        // Then
        val recipes = repository.getRecipes().first()
        assertEquals(2, recipes.size)
        assertTrue(recipes.any { it.id == id1 })
        assertTrue(recipes.any { it.id == id2 })
    }

    @Test
    fun testDeleteRecipeRemoves() = runTest {
        // Given
        val repository = FakeRecipeRepository()
        val recipe = createSampleRecipe("Recipe to delete")
        val id = repository.saveRecipe(recipe)

        // When
        val result = repository.deleteRecipe(id)

        // Then
        assertTrue(result)
        val deletedRecipe = repository.getRecipeById(id)
        assertEquals(null, deletedRecipe)
    }

    @Test
    fun testUpdateFavoriteStatus() = runTest {
        // Given
        val repository = FakeRecipeRepository()
        val recipe = createSampleRecipe("Favorite Recipe")
        val id = repository.saveRecipe(recipe)

        // When
        val result = repository.updateFavoriteStatus(id, true)

        // Then
        assertTrue(result)
        val updatedRecipe = repository.getRecipeById(id)
        assertNotNull(updatedRecipe)
        assertTrue(updatedRecipe.favorite)
    }

    @Test
    fun testSearchRecipes() = runTest {
        // Given
        val repository = FakeRecipeRepository()
        repository.saveRecipe(createSampleRecipe("Mojito"))
        repository.saveRecipe(createSampleRecipe("Margarita"))
        repository.saveRecipe(createSampleRecipe("Old Fashioned"))

        // When
        val results = repository.searchRecipes("mar").first()

        // Then
        assertEquals(1, results.size)
        assertEquals("Margarita", results.first().name)
    }

    @Test
    fun testGetFavoriteRecipes() = runTest {
        // Given
        val repository = FakeRecipeRepository()
        val id1 = repository.saveRecipe(createSampleRecipe("Recipe 1"))
        val id2 = repository.saveRecipe(createSampleRecipe("Recipe 2"))
        val id3 = repository.saveRecipe(createSampleRecipe("Recipe 3"))

        // When
        repository.updateFavoriteStatus(id1, true)
        repository.updateFavoriteStatus(id3, true)

        // Then
        val favorites = repository.getFavoriteRecipes().first()
        assertEquals(2, favorites.size)
        assertTrue(favorites.all { it.favorite })
        assertTrue(favorites.any { it.id == id1 })
        assertTrue(favorites.any { it.id == id3 })
    }
}
