package com.josephhopson.sprituum

import android.content.Context
import androidx.compose.runtime.compositionLocalOf

/**
 * Application context for Android
 */
private var applicationContext: Context? = null

/**
 * Set the application context
 */
fun setApplicationContext(context: Context) {
    applicationContext = context
}

/**
 * Get the platform context
 */
fun getPlatformContext(): Context {
    return applicationContext
        ?: throw IllegalStateException("Application context not set")
}

/**
 * Composition local for the platform context
 */
val LocalPlatformContext = compositionLocalOf<Context> {
    error("No platform context provided")
}