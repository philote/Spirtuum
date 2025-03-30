package com.josephhopson.sprituum.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for Recipe entity
 *
 * All methods are either suspend functions or return Flow for Kotlin Multiplatform compatibility
 */
@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY name ASC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: Long): RecipeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity): Long

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity): Int

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity): Int

    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteRecipeById(id: Long): Int

    @Query("UPDATE recipes SET favorite = :favorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, favorite: Boolean): Int

    @Query("SELECT * FROM recipes WHERE favorite = 1 ORDER BY name ASC")
    fun getFavoriteRecipes(): Flow<List<RecipeEntity>>

    @Query(
        "SELECT * FROM recipes WHERE " +
                "name LIKE '%' || :query || '%' OR " +
                "altName LIKE '%' || :query || '%' OR " +
                "about LIKE '%' || :query || '%' " +
                "ORDER BY name ASC"
    )
    fun searchRecipes(query: String): Flow<List<RecipeEntity>>
}

/**
 * Room DAO for Recipe Tag entity
 */
@Dao
interface RecipeTagDao {
    @Query("SELECT * FROM recipe_tags WHERE recipeId = :recipeId")
    fun getTagsForRecipe(recipeId: Long): Flow<List<RecipeTagEntity>>

    @Query("SELECT * FROM recipe_tags WHERE recipeId = :recipeId")
    suspend fun getTagsForRecipeSync(recipeId: Long): List<RecipeTagEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<RecipeTagEntity>)

    @Query("DELETE FROM recipe_tags WHERE recipeId = :recipeId")
    suspend fun deleteTagsForRecipe(recipeId: Long): Int

    @Query(
        "SELECT * FROM recipes " +
                "INNER JOIN recipe_tags ON recipes.id = recipe_tags.recipeId " +
                "WHERE recipe_tags.tag = :tag"
    )
    fun getRecipesWithTag(tag: String): Flow<List<RecipeEntity>>
}

/**
 * Room DAO for Recipe Instruction entity
 */
@Dao
interface RecipeInstructionDao {
    @Query("SELECT * FROM recipe_instructions WHERE recipeId = :recipeId ORDER BY step ASC")
    fun getInstructionsForRecipe(recipeId: Long): Flow<List<RecipeInstructionEntity>>

    @Query("SELECT * FROM recipe_instructions WHERE recipeId = :recipeId ORDER BY step ASC")
    suspend fun getInstructionsForRecipeSync(recipeId: Long): List<RecipeInstructionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstructions(instructions: List<RecipeInstructionEntity>)

    @Query("DELETE FROM recipe_instructions WHERE recipeId = :recipeId")
    suspend fun deleteInstructionsForRecipe(recipeId: Long): Int
}

/**
 * Room DAO for Recipe Ingredient entity
 */
@Dao
interface RecipeIngredientDao {
    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    fun getIngredientsForRecipe(recipeId: Long): Flow<List<RecipeIngredientEntity>>

    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    suspend fun getIngredientsForRecipeSync(recipeId: Long): List<RecipeIngredientEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<RecipeIngredientEntity>)

    @Query("DELETE FROM recipe_ingredients WHERE recipeId = :recipeId")
    suspend fun deleteIngredientsForRecipe(recipeId: Long): Int

    @Query(
        "SELECT * FROM recipes " +
                "INNER JOIN recipe_ingredients ON recipes.id = recipe_ingredients.recipeId " +
                "WHERE recipe_ingredients.name LIKE '%' || :ingredientName || '%'"
    )
    fun getRecipesWithIngredient(ingredientName: String): Flow<List<RecipeEntity>>
}
