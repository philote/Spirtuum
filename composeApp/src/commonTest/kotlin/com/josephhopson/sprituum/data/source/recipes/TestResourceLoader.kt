package com.josephhopson.sprituum.data.source.recipes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Test implementation of resource loading
 *
 * This is used instead of the actual platform implementations during tests
 */
suspend fun loadTestResourceAsString(path: String): String = withContext(Dispatchers.Default) {
    // In tests, we'll just return a mock recipe JSON
    """
    {
      "name": "Mock Recipe",
      "about": "This is a mock recipe for testing",
      "tags": ["test"],
      "instructions": ["Test instruction"],
      "alcoholic": true,
      "ingredients": [{"name": "Test Ingredient", "amount": 1.0, "unit": "oz"}]
    }
    """
}
