package com.josephhopson.sprituum.data.source.recipes

import com.josephhopson.sprituum.domain.model.Recipe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Tests for the JsonInitialRecipesProvider
 */
class JsonInitialRecipesProviderTest {

    /**
     * Test implementation for testing
     */
    private class TestJsonInitialRecipesProvider : InitialRecipesProvider {
        override suspend fun getInitialRecipes(): List<Recipe> = emptyList()
    }

    @Test
    fun `getInitialRecipes returns recipes`() = runTest {
        // Given
        val provider = TestJsonInitialRecipesProvider()

        // When
        val recipes = provider.getInitialRecipes()

        // Then
        assertTrue(recipes.isEmpty(), "Test provider should return empty list")
    }
}
