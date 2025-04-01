package com.josephhopson.sprituum

import android.app.Application
import com.josephhopson.sprituum.di.AndroidPlatformModule
import com.josephhopson.sprituum.di.AppModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Android Application class for Spirituum app
 */
class SpirituumApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize the platform context
        setApplicationContext(this)
        // Initialize Koin
        initKoin()
    }
    
    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@SpirituumApp)
            modules(AppModules.allModules + AndroidPlatformModule.create(this@SpirituumApp))
        }
    }
}