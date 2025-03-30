package com.josephhopson.sprituum.di

import android.content.Context
import com.josephhopson.sprituum.data.source.local.AndroidDatabaseProvider
import com.josephhopson.sprituum.data.source.local.AppDatabase
import com.josephhopson.sprituum.data.source.local.DatabaseProvider
import com.josephhopson.sprituum.data.source.preference.AndroidSettingsProvider
import com.josephhopson.sprituum.data.source.preference.SettingsProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific dependency injection module
 */
object AndroidPlatformModule {

    /**
     * Android platform dependencies
     */
    fun create(context: Context) = module {
        // Database provider
        single<DatabaseProvider> { AndroidDatabaseProvider(context) }
        single<AppDatabase> { get<DatabaseProvider>().provideDatabase() }

        // Settings provider
        single<SettingsProvider> { AndroidSettingsProvider(context) }
    }
}