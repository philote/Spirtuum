@file:OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsApi::class)

package com.josephhopson.sprituum.di

import com.josephhopson.sprituum.data.repository.RoomRecipeRepository
import com.josephhopson.sprituum.data.repository.SettingsUserPreferencesRepository
import com.josephhopson.sprituum.data.source.preference.SettingsProvider
import com.josephhopson.sprituum.domain.repository.RecipeRepository
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository
import com.josephhopson.sprituum.domain.usecase.DeleteRecipeUseCase
import com.josephhopson.sprituum.domain.usecase.DeleteRecipeUseCaseImpl
import com.josephhopson.sprituum.domain.usecase.GetFavoriteRecipesUseCase
import com.josephhopson.sprituum.domain.usecase.GetFavoriteRecipesUseCaseImpl
import com.josephhopson.sprituum.domain.usecase.GetRecipeByIdUseCase
import com.josephhopson.sprituum.domain.usecase.GetRecipeByIdUseCaseImpl
import com.josephhopson.sprituum.domain.usecase.GetRecipesUseCase
import com.josephhopson.sprituum.domain.usecase.GetRecipesUseCaseImpl
import com.josephhopson.sprituum.domain.usecase.SaveRecipeUseCase
import com.josephhopson.sprituum.domain.usecase.SaveRecipeUseCaseImpl
import com.josephhopson.sprituum.domain.usecase.SearchRecipesUseCase
import com.josephhopson.sprituum.domain.usecase.SearchRecipesUseCaseImpl
import com.josephhopson.sprituum.domain.usecase.ToggleFavoriteRecipeUseCase
import com.josephhopson.sprituum.domain.usecase.ToggleFavoriteRecipeUseCaseImpl
import com.russhwolf.settings.ExperimentalSettingsApi
import org.koin.dsl.module

/**
 * Application dependency injection modules
 */
object AppModules {

    /**
     * Data layer dependencies
     */
    val dataModule = module {
        // Repositories
        single<RecipeRepository> { RoomRecipeRepository(database = get()) }
        single<UserPreferencesRepository> { SettingsUserPreferencesRepository(settings = get<SettingsProvider>().provideSettings()) }
    }

    /**
     * Domain layer dependencies
     */
    val domainModule = module {
        // Recipe use cases
        single<GetRecipesUseCase> { GetRecipesUseCaseImpl(get(), get()) }
        single<GetRecipeByIdUseCase> { GetRecipeByIdUseCaseImpl(get()) }
        single<SaveRecipeUseCase> { SaveRecipeUseCaseImpl(get()) }
        single<DeleteRecipeUseCase> { DeleteRecipeUseCaseImpl(get()) }
        single<ToggleFavoriteRecipeUseCase> { ToggleFavoriteRecipeUseCaseImpl(get()) }
        single<SearchRecipesUseCase> { SearchRecipesUseCaseImpl(get()) }
        single<GetFavoriteRecipesUseCase> { GetFavoriteRecipesUseCaseImpl(get()) }
    }

    /**
     * UI layer dependencies
     */
    val uiModule = module {
        // ViewModels will be added here
    }

    /**
     * All application modules combined
     */
    val allModules = listOf(
        dataModule,
        domainModule,
        uiModule
    )
}
