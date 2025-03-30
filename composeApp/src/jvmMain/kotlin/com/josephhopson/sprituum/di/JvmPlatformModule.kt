package com.josephhopson.sprituum.di

import com.josephhopson.sprituum.data.source.local.AppDatabase
import com.josephhopson.sprituum.data.source.local.DatabaseProvider
import com.josephhopson.sprituum.data.source.preference.JvmSettingsProvider
import com.josephhopson.sprituum.data.source.preference.SettingsProvider
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

        // TODO: Implement JVM-specific database provider when implementing desktop target
    }
}