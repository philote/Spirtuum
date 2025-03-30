@file:OptIn(ExperimentalCoroutinesApi::class)

package com.josephhopson.sprituum.domain.usecase

import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.domain.repository.RecipeRepository
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.FilterOption
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.SortOption
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

/**
 * Implementation of GetRecipesUseCase
 */
class GetRecipesUseCaseImpl(
    private val recipeRepository: RecipeRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : GetRecipesUseCase {

    override fun invoke(): Flow<List<Recipe>> {
        val filterFlow = userPreferencesRepository.getFilterOption()
        val sortFlow = userPreferencesRepository.getSortOption()

        return combine(
            filterFlow,
            sortFlow
        ) { filter, sort ->
            filter to sort
        }.flatMapLatest { (filter, sort) ->
            // Apply filtering
            val recipesFlow = when (filter) {
                FilterOption.ALL -> recipeRepository.getRecipes()
                FilterOption.FAVORITES -> recipeRepository.getFavoriteRecipes()
                FilterOption.ALCOHOLIC -> recipeRepository.getRecipes().map { recipes ->
                    recipes.filter { it.alcoholic }
                }

                FilterOption.NON_ALCOHOLIC -> recipeRepository.getRecipes().map { recipes ->
                    recipes.filter { !it.alcoholic }
                }
            }

            // Apply sorting
            recipesFlow.map { recipes ->
                when (sort) {
                    SortOption.NAME_ASC -> recipes.sortedBy { it.name }
                    SortOption.NAME_DESC -> recipes.sortedByDescending { it.name }
                    SortOption.DATE_CREATED_NEWEST -> recipes.sortedByDescending { it.createdAt }
                    SortOption.DATE_CREATED_OLDEST -> recipes.sortedBy { it.createdAt }
                    SortOption.DATE_UPDATED_NEWEST -> recipes.sortedByDescending { it.updatedAt }
                    SortOption.DATE_UPDATED_OLDEST -> recipes.sortedBy { it.updatedAt }
                }
            }
        }
    }
}