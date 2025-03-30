package com.josephhopson.sprituum.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Main Room database for the application
 */
@Database(
    entities = [
        RecipeEntity::class,
        RecipeTagEntity::class,
        RecipeInstructionEntity::class,
        RecipeIngredientEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Get the Recipe DAO
     */
    abstract fun recipeDao(): RecipeDao

    /**
     * Get the Recipe Tag DAO
     */
    abstract fun recipeTagDao(): RecipeTagDao

    /**
     * Get the Recipe Instruction DAO
     */
    abstract fun recipeInstructionDao(): RecipeInstructionDao

    /**
     * Get the Recipe Ingredient DAO
     */
    abstract fun recipeIngredientDao(): RecipeIngredientDao
}
