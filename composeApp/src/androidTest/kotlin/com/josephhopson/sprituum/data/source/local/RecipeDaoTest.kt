package com.josephhopson.sprituum.data.source.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Instrumented test for the Room database
 *
 * These tests verify that our Room implementation works correctly on Android.
 * Run this test on an Android device or emulator.
 */
@RunWith(AndroidJUnit4::class)
class RecipeDaoTest {

    private lateinit var recipeDao: RecipeDao
    private lateinit var db: AppDatabase
    private lateinit var tagDao: RecipeTagDao
    private lateinit var instructionDao: RecipeInstructionDao
    private lateinit var ingredientDao: RecipeIngredientDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        recipeDao = db.recipeDao()
        tagDao = db.recipeTagDao()
        instructionDao = db.recipeInstructionDao()
        ingredientDao = db.recipeIngredientDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndGetRecipe() = runTest {
        // Create a recipe entity
        val recipeEntity = RecipeEntity(
            name = "Mojito",
            altName = "Cuban Mojito",
            favorite = false,
            about = "A refreshing Cuban cocktail",
            alcoholic = true,
            glassware = "Highball glass",
            garnish = "Mint sprig",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        // Insert the recipe
        val recipeId = recipeDao.insertRecipe(recipeEntity)

        // Insert some tags
        val tags = listOf(
            RecipeTagEntity(recipeId = recipeId, tag = "Cuban"),
            RecipeTagEntity(recipeId = recipeId, tag = "Refreshing")
        )
        tagDao.insertTags(tags)

        // Insert instructions
        val instructions = listOf(
            RecipeInstructionEntity(recipeId = recipeId, step = 1, value = "Muddle mint leaves"),
            RecipeInstructionEntity(recipeId = recipeId, step = 2, value = "Add rum and ice")
        )
        instructionDao.insertInstructions(instructions)

        // Insert ingredients
        val ingredients = listOf(
            RecipeIngredientEntity(
                recipeId = recipeId,
                name = "White rum",
                amountValue = 60.0,
                amountLabel = "ml"
            )
        )
        ingredientDao.insertIngredients(ingredients)

        // Retrieve the recipe from the database
        val retrievedRecipe = recipeDao.getRecipeById(recipeId)

        // Verify it matches what we inserted
        assertEquals("Mojito", retrievedRecipe?.name)
        assertEquals("Cuban Mojito", retrievedRecipe?.altName)
        assertEquals(true, retrievedRecipe?.alcoholic)

        // Retrieve and verify tags
        val retrievedTags = tagDao.getTagsForRecipeSync(recipeId)
        assertEquals(2, retrievedTags.size)
        assertTrue(retrievedTags.any { it.tag == "Cuban" })

        // Retrieve and verify instructions
        val retrievedInstructions = instructionDao.getInstructionsForRecipeSync(recipeId)
        assertEquals(2, retrievedInstructions.size)
        assertEquals(1, retrievedInstructions[0].step)

        // Retrieve and verify ingredients
        val retrievedIngredients = ingredientDao.getIngredientsForRecipeSync(recipeId)
        assertEquals(1, retrievedIngredients.size)
        assertEquals("White rum", retrievedIngredients[0].name)
        assertEquals(60.0, retrievedIngredients[0].amountValue)
    }

    @Test
    fun testUpdateFavoriteStatus() = runTest {
        // Create and insert a recipe
        val recipeEntity = RecipeEntity(
            name = "Margarita",
            alcoholic = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val recipeId = recipeDao.insertRecipe(recipeEntity)

        // Update favorite status
        recipeDao.updateFavoriteStatus(recipeId, true)

        // Verify the update
        val updatedRecipe = recipeDao.getRecipeById(recipeId)
        assertEquals(true, updatedRecipe?.favorite)
    }

    @Test
    fun testDeleteRecipe() = runTest {
        // Create and insert a recipe
        val recipeEntity = RecipeEntity(
            name = "Moscow Mule",
            alcoholic = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val recipeId = recipeDao.insertRecipe(recipeEntity)

        // Delete the recipe
        recipeDao.deleteRecipeById(recipeId)

        // Verify it's gone
        val deletedRecipe = recipeDao.getRecipeById(recipeId)
        assertNull(deletedRecipe)
    }

    @Test
    fun testSearchRecipes() = runTest {
        // Insert several recipes
        val recipe1 = RecipeEntity(
            name = "Mojito",
            altName = null,
            about = "A refreshing drink",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val recipe2 = RecipeEntity(
            name = "Mai Tai",
            altName = null,
            about = "A tropical cocktail",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val recipe3 = RecipeEntity(
            name = "Old Fashioned",
            altName = null,
            about = "A classic whiskey cocktail",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        recipeDao.insertRecipe(recipe1)
        recipeDao.insertRecipe(recipe2)
        recipeDao.insertRecipe(recipe3)

        // Search for recipes with "mojito"
        val mojitoResults = recipeDao.searchRecipes("mojito").first()
        assertEquals(1, mojitoResults.size)
        assertEquals("Mojito", mojitoResults[0].name)

        // Search for recipes with "cocktail" in the about text
        val cocktailResults = recipeDao.searchRecipes("cocktail").first()
        assertEquals(2, cocktailResults.size)
    }
}
