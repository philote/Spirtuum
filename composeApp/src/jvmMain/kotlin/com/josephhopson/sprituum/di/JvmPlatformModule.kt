package com.josephhopson.sprituum.di

import com.josephhopson.sprituum.data.source.local.DatabaseProvider
import com.josephhopson.sprituum.data.source.preference.JvmSettingsProvider
import com.josephhopson.sprituum.data.source.preference.SettingsProvider
import com.josephhopson.sprituum.data.source.local.JvmDatabaseProvider
import org.koin.dsl.module

/**
 * JVM-specific dependency injection module for desktop platforms
 */
object JvmPlatformModule {

    /**
     * JVM platform dependencies
     */
    val module = module {
        // Settings provider
        single<SettingsProvider> { JvmSettingsProvider() }

        // Database provider
        single<DatabaseProvider> { JvmDatabaseProvider() }
        single { get<DatabaseProvider>().provideDatabase() }
    }
}