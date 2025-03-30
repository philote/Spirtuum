package com.josephhopson.sprituum.di

import com.josephhopson.sprituum.data.repository.RoomRecipeRepository
import com.josephhopson.sprituum.domain.repository.RecipeRepository
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
    }

    /**
     * Domain layer dependencies
     */
    val domainModule = module {
        // Use cases will be added here
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
