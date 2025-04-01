package com.josephhopson.sprituum.data.source.recipes

import com.josephhopson.sprituum.domain.model.Amount
import com.josephhopson.sprituum.domain.model.Ingredient
import com.josephhopson.sprituum.domain.model.Instruction
import com.josephhopson.sprituum.domain.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Platform-specific resource loading
 */
expect suspend fun loadResourceAsString(path: String): String

/**
 * Implementation of InitialRecipesProvider that loads recipes from JSON resources
 */
class JsonInitialRecipesProvider : InitialRecipesProvider {

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Returns a list of initial recipes loaded from JSON resources
     */
    override suspend fun getInitialRecipes(): List<Recipe> = withContext(Dispatchers.Default) {
        val recipeFiles = listOf(
            "manhattan.json",
            "old_fashioned.json",
            "daiquiri.json",
            "martini.json",
            "negroni.json",
            "whiskey_sour.json",
            "sazerac.json",
            "mai_tai.json",
            "mint_julep.json",
            "mojito.json"
        )

        val now = Clock.System.now()

        recipeFiles.mapNotNull { fileName ->
            try {
                loadRecipeFromResource("recipes/$fileName", now)
            } catch (e: Exception) {
                println("Failed to load recipe from $fileName: ${e.message}")
                null
            }
        }
    }

    /**
     * Loads a recipe from a resource file
     * @param resourcePath The path to the resource file
     * @param timestamp The timestamp to use for createdAt and updatedAt
     * @return The loaded Recipe
     */
    private suspend fun loadRecipeFromResource(resourcePath: String, timestamp: Instant): Recipe =
        withContext(Dispatchers.Default) {
            val resourceContent = getResourceContent(resourcePath)
            val recipeJson = json.decodeFromString<RecipeJson>(resourceContent)

            Recipe(
                id = 0, // Will be assigned by the database
                name = recipeJson.name,
                altName = recipeJson.altName,
                favorite = recipeJson.favorite ?: false,
                imagePath = recipeJson.imagePath,
                about = recipeJson.about,
                tags = recipeJson.tags ?: emptyList(),
                instructions = recipeJson.instructions?.mapIndexed { index, instruction ->
                    Instruction(
                        step = index + 1,
                        value = instruction
                    )
                } ?: emptyList(),
                notes = recipeJson.notes,
                alcoholic = recipeJson.alcoholic ?: true,
                glassware = recipeJson.glassware,
                garnish = recipeJson.garnish,
                ingredients = recipeJson.ingredients?.map { ingredient ->
                    Ingredient(
                        name = ingredient.name,
                        amount = if (ingredient.amount != null && ingredient.unit != null) {
                            Amount(
                                value = ingredient.amount,
                                label = ingredient.unit
                            )
                        } else null,
                        notes = ingredient.notes
                    )
                } ?: emptyList(),
                createdAt = timestamp,
                updatedAt = timestamp
            )
        }

    /**
     * Gets the content of a resource file
     * @param path The path to the resource file
     * @return The content of the resource file as a string
     */
    private suspend fun getResourceContent(path: String): String =
        withContext(Dispatchers.Default) {
            loadResourceAsString(path)
        }

    /**
     * JSON representation of a Recipe for deserialization
     */
    @Serializable
    private data class RecipeJson(
        val name: String,
        val altName: String? = null,
        val favorite: Boolean? = null,
        val imagePath: String? = null,
        val about: String? = null,
        val tags: List<String>? = null,
        val instructions: List<String>? = null,
        val notes: String? = null,
        val alcoholic: Boolean? = null,
        val glassware: String? = null,
        val garnish: String? = null,
        val ingredients: List<IngredientJson>? = null
    )

    /**
     * JSON representation of an Ingredient for deserialization
     */
    @Serializable
    private data class IngredientJson(
        val name: String,
        val amount: Double? = null,
        val unit: String? = null,
        val notes: String? = null
    )
}
