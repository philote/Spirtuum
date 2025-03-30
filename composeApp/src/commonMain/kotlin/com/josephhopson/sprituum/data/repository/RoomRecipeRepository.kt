package com.josephhopson.sprituum.data.repository

import com.josephhopson.sprituum.data.model.mapper.toRecipe
import com.josephhopson.sprituum.data.source.local.AppDatabase
import com.josephhopson.sprituum.data.source.local.RecipeEntity
import com.josephhopson.sprituum.data.source.local.RecipeIngredientEntity
import com.josephhopson.sprituum.data.source.local.RecipeInstructionEntity
import com.josephhopson.sprituum.data.source.local.RecipeTagEntity
import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

/**
 * Implementation of RecipeRepository that uses Room database for storage
 */
class RoomRecipeRepository(
    private val database: AppDatabase
) : RecipeRepository {

    override fun getRecipes(): Flow<List<Recipe>> {
        val recipeDao = database.recipeDao()
        val tagDao = database.recipeTagDao()
        val instructionDao = database.recipeInstructionDao()
        val ingredientDao = database.recipeIngredientDao()

        return recipeDao.getAllRecipes().map { recipes ->
            recipes.map { recipe ->
                val tags = tagDao.getTagsForRecipeSync(recipe.id)
                val instructions = instructionDao.getInstructionsForRecipeSync(recipe.id)
                val ingredients = ingredientDao.getIngredientsForRecipeSync(recipe.id)

                recipe.toRecipeDto(tags, instructions, ingredients).toRecipe()
            }
        }
    }

    override suspend fun getRecipeById(id: Long): Recipe? {
        val recipeDao = database.recipeDao()
        val tagDao = database.recipeTagDao()
        val instructionDao = database.recipeInstructionDao()
        val ingredientDao = database.recipeIngredientDao()

        val recipe = recipeDao.getRecipeById(id) ?: return null
        val tags = tagDao.getTagsForRecipeSync(recipe.id)
        val instructions = instructionDao.getInstructionsForRecipeSync(recipe.id)
        val ingredients = ingredientDao.getIngredientsForRecipeSync(recipe.id)

        return recipe.toRecipeDto(tags, instructions, ingredients).toRecipe()
    }

    override suspend fun saveRecipe(recipe: Recipe): Long {
        val recipeDao = database.recipeDao()
        val tagDao = database.recipeTagDao()
        val instructionDao = database.recipeInstructionDao()
        val ingredientDao = database.recipeIngredientDao()

        // Convert domain Recipe to RecipeEntity
        val now = Clock.System.now().toEpochMilliseconds()
        val createdAt = if (recipe.id == 0L) now else recipe.createdAt.toEpochMilliseconds()

        val recipeEntity = RecipeEntity(
            id = recipe.id,
            name = recipe.name,
            altName = recipe.altName,
            favorite = recipe.favorite,
            imagePath = recipe.imagePath,
            about = recipe.about,
            alcoholic = recipe.alcoholic,
            glassware = recipe.glassware,
            garnish = recipe.garnish,
            notes = recipe.notes,
            createdAt = createdAt,
            updatedAt = now
        )

        // Insert or update recipe entity
        val recipeId = recipeDao.insertRecipe(recipeEntity)

        // Delete existing related data and insert new data
        tagDao.deleteTagsForRecipe(recipeId)
        instructionDao.deleteInstructionsForRecipe(recipeId)
        ingredientDao.deleteIngredientsForRecipe(recipeId)

        // Insert new tags
        val tagEntities = recipe.tags.map { tag ->
            RecipeTagEntity(recipeId = recipeId, tag = tag)
        }
        tagDao.insertTags(tagEntities)

        // Insert new instructions
        val instructionEntities = recipe.instructions.map { instruction ->
            RecipeInstructionEntity(
                recipeId = recipeId,
                step = instruction.step,
                value = instruction.value
            )
        }
        instructionDao.insertInstructions(instructionEntities)

        // Insert new ingredients
        val ingredientEntities = recipe.ingredients.map { ingredient ->
            RecipeIngredientEntity(
                recipeId = recipeId,
                name = ingredient.name,
                amountValue = ingredient.amount?.value,
                amountLabel = ingredient.amount?.label,
                notes = ingredient.notes
            )
        }
        ingredientDao.insertIngredients(ingredientEntities)

        return recipeId
    }

    override suspend fun deleteRecipe(id: Long): Boolean {
        val recipeDao = database.recipeDao()
        val tagDao = database.recipeTagDao()
        val instructionDao = database.recipeInstructionDao()
        val ingredientDao = database.recipeIngredientDao()

        // Delete related data first to maintain referential integrity
        tagDao.deleteTagsForRecipe(id)
        instructionDao.deleteInstructionsForRecipe(id)
        ingredientDao.deleteIngredientsForRecipe(id)

        // Delete the recipe
        val result = recipeDao.deleteRecipeById(id)
        return result > 0
    }

    override suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean): Boolean {
        val recipeDao = database.recipeDao()
        val result = recipeDao.updateFavoriteStatus(id, isFavorite)
        return result > 0
    }

    override fun searchRecipes(query: String): Flow<List<Recipe>> {
        val recipeDao = database.recipeDao()
        val tagDao = database.recipeTagDao()
        val instructionDao = database.recipeInstructionDao()
        val ingredientDao = database.recipeIngredientDao()

        return recipeDao.searchRecipes(query).map { recipes ->
            recipes.map { recipe ->
                val tags = tagDao.getTagsForRecipeSync(recipe.id)
                val instructions = instructionDao.getInstructionsForRecipeSync(recipe.id)
                val ingredients = ingredientDao.getIngredientsForRecipeSync(recipe.id)

                recipe.toRecipeDto(tags, instructions, ingredients).toRecipe()
            }
        }
    }

    override fun getFavoriteRecipes(): Flow<List<Recipe>> {
        val recipeDao = database.recipeDao()
        val tagDao = database.recipeTagDao()
        val instructionDao = database.recipeInstructionDao()
        val ingredientDao = database.recipeIngredientDao()

        return recipeDao.getFavoriteRecipes().map { recipes ->
            recipes.map { recipe ->
                val tags = tagDao.getTagsForRecipeSync(recipe.id)
                val instructions = instructionDao.getInstructionsForRecipeSync(recipe.id)
                val ingredients = ingredientDao.getIngredientsForRecipeSync(recipe.id)

                recipe.toRecipeDto(tags, instructions, ingredients).toRecipe()
            }
        }
    }
}
