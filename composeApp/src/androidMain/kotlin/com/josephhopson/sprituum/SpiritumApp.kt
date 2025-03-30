package com.josephhopson.sprituum

import android.app.Application
import com.josephhopson.sprituum.data.source.local.AndroidDatabaseProvider
import com.josephhopson.sprituum.data.source.local.DatabaseProvider
import com.josephhopson.sprituum.di.AppModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Android Application class for Spirituum app
 */
class SpirituumApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
    
    private fun initKoin() {
        val androidModule = module {
            single<DatabaseProvider> { AndroidDatabaseProvider(this@SpirituumApp) }
            single { get<DatabaseProvider>().provideDatabase() }
        }

        startKoin {
            androidLogger()
            androidContext(this@SpirituumApp)
            modules(AppModules.allModules + androidModule)
        }
    }
}
