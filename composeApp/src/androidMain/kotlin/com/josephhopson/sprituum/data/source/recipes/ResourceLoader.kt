package com.josephhopson.sprituum.data.source.recipes

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Android implementation of resource loading
 */
actual suspend fun loadResourceAsString(path: String): String = withContext(Dispatchers.IO) {
    val resourceReader = AndroidResourceReader()
    resourceReader.readResource(path)
}

/**
 * Helper class to read resources on Android
 */
private class AndroidResourceReader : KoinComponent {
    private val context: Context by inject()

    /**
     * Reads a resource file from assets
     */
    fun readResource(path: String): String {
        return context.assets.open(path).bufferedReader().use { it.readText() }
    }
}
